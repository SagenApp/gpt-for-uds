# Gpt for Uds

This project aims to make ChatGPT more accessible for application developers by having a daemon running in the background exposing a Unix Domain Socket file that other applications can use to interact with ChatGPT without having to do any api calls themself.

Please note that this is not even a proof of concept at this point in time.

If you want to contribute I'm open for anything.

## Current architecture

### The binary itself
The program is written in Java and compiled to a native binary executable through GraalVM. It is still a lot bigger than it would be if written in Rust and this is a plan for the future.

The application starts up with 2 initial parameters, where to create the Unix Domain Socket file and a token to ChatGPT. The application will at startup create a new socket file and start listening to this. Once any client connects and writes a json with a list of messages it will forward this to the official ChatGPT api using the provided token and stream the resulting text back as is.

### The socket protocol
The protocol is not perfect but its good enough for now.
The protocol starts with the client first creating a String with json for the messages to send to ChatGPT. This includes chat history to allow for a coversation like usage.

The client will then send a 4 byte signed integer with the length of the json string.
Then the client will send the entire json string.

The server will after reading the provided length pass the entire json to ChatGPT and stream the response back.
For each chunk of data from ChatGPT it will write a signed 4 byte int with the length of the chunk and then the chunk as raw text. The server will close the connection once completed.

### What can this be used for?
I plan to create some new application for popping up a ChatGPT window with a hotkey making ChatGPT always available on demand in my custom installation. I didnt want to bother with the network requests in this application so I created this more generic linux api for it instead.
