import socket
import json
import struct

# Path to the Unix domain socket
SOCKET_PATH = "/tmp/gpt_socket"

def send_message(sock, message):
    """Send a message which is a JSON encoded dictionary with a 4-byte length prefix."""
    encoded_message = json.dumps(message).encode('utf-8')
    message_length = struct.pack('!I', len(encoded_message))
    sock.sendall(message_length + encoded_message)

def receive_response(sock):
    """Receive messages from the server and print them."""
    while True:
        # First read the length of the message
        length_data = sock.recv(4)
        if not length_data:
            print("\n\nMission completed\nServer closed connection")
            return  # Exit the loop and therefore the function
        length = struct.unpack('!I', length_data)[0]

        # Now read the message of the given length
        message_data = b''
        while len(message_data) < length:
            to_read = length - len(message_data)
            message_data += sock.recv(to_read)

        # Since server sends each response with '\n', we strip it for consistent output.
        print(message_data.decode('utf-8'), end ="", flush=True)

def main():
    # Create a UDS (Unix Domain Socket)
    sock = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)

    # Connect to the server
    try:
        print(f"Connecting to {SOCKET_PATH}...")
        sock.connect(SOCKET_PATH)
    except socket.error as e:
        print(f"Failed to connect to {SOCKET_PATH}: {e}")
        return

    print("Connected\n")

    # Construct a message in the format expected by the server
    messages = [
        {"role": "system", "content": "Please answer the user with a very short answer. Around 10 words."},
        {"role": "user", "content": "Can you tell me about the moon?"},
    ]

    # Send the message
    send_message(sock, messages)

    # Receive and print the response
    receive_response(sock)

    # Close the connection
    sock.close()
    print("Connection closed")

if __name__ == "__main__":
    main()
