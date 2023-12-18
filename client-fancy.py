import socket
import json
import struct

# Path to the Unix domain socket
SOCKET_PATH = "/tmp/gpt_socket"

def send_messages(sock, messages):
    """Send an array of messages which are JSON encoded with a 4-byte length prefix."""
    encoded_messages = json.dumps(messages).encode('utf-8')
    messages_length = struct.pack('!I', len(encoded_messages))
    sock.sendall(messages_length + encoded_messages)

def receive_response(sock):
    """Receive messages from the server and print them."""
    full_response = ""
    try:
        while True:
            # First read the length of the message
            length_data = sock.recv(4)
            if not length_data:
                break
            length = struct.unpack('!I', length_data)[0]

            # Now read the message of the given length
            message_data = b''
            while len(message_data) < length:
                to_read = length - len(message_data)
                message_data += sock.recv(to_read)

            # Append to full response string
            full_response += message_data.decode('utf-8')

    except ConnectionResetError:
        print("\nConnection was reset by the server.")
    finally:
        print("\n\nMission completed")
        return full_response  # Return the full concatenated response


def interact_with_server(message_history):
    """Create a socket connection to the server, send the message history, and receive a response."""
    with socket.socket(socket.AF_UNIX, socket.SOCK_STREAM) as sock:
        sock.connect(SOCKET_PATH)
        send_messages(sock, message_history)
        return receive_response(sock)

def main():
    message_history = [
        {"role": "system", "content": "Please provide a short response."}
    ]
    user_message = {"role": "user", "content": "Can you tell me about the moon?"}

    message_history.append(user_message)
    response = interact_with_server(message_history)

    if response:
        print(response)
        message_history.append({"role": "assistant", "content": response})

    # Display the full conversation history
    print("\nFull conversation history:")
    for message in message_history:
        if isinstance(message, dict):
            role, content = next(iter(message.items()))
            print(f"{role.capitalize()}: {content}")
        else:
            print(f"Assistant: {message}")

    print("\nConnection closed")

if __name__ == "__main__":
    main()
