export const SimpleWebSocket = () => {

    let socket = null;
    let handler;

    const connect =  (host) => {
        if ('WebSocket' in window) {
            socket = new WebSocket(host);
        } else {
            console.log('Error: WebSocket is not supported by this browser.');
            return;
        }

        socket.onopen = () => {
            console.log('Info: WebSocket connection opened.');
            if (handler) {
                handler.onopen();
            }
        };

        socket.onclose = () => {
            console.log('Info: WebSocket closed.');
            if (handler) {
                handler.onclose();
            }
        };

        socket.onmessage =  (message) => {
            // console.log("response: " + message.data);
            if (handler) {
                handler.onmessage(message.data);
            }
        };
    };

    const initialize = (callback, ep) => {
        handler = callback;
        if (!ep) ep = '/react';
        if (window.location.protocol === 'http:') {
            connect('ws://' + window.location.host + ep);
        } else {
            connect('wss://' + window.location.host + ep);
        }
    };

    const sendMessage = (message) => {
        if (socket) {
            socket.send(JSON.stringify(message));
        }
    };

    const sendBinary = (message) => {
        if (socket) {
            socket.send(message);
        }
    };

    return { initialize, sendMessage, sendBinary };
};
