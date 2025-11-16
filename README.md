# spring5-react-isomorphic-websockets-mongo
## TL;DR
Redux Todo demo with the redux state kept on the server. The server is a Spring Boot 2 application, using Spring 5 Webflux and Websockets.
It does **server side rendering** of the **React** client using **Nashorn**. The rendering process is triggered at startup and on state changes, and the rendered html is cached. 
Every change in the client application is sent to the server via websockets. The server then updates all clients that are connected to it.
**So you can have multiple tabs of the browser with the application loaded (even different browsers) and 
all of them are kept in sync at all times.**
If the rendering process is not finished, a client side rendered version is sent to the browser, thus no stale html is ever rendered.
The infrastructure is not based on reactive programming anymore. The reason for this is my intent to simplify the code, now that we can use virtual threads.
It uses **MongoDB** to store the redux state.
The client npm build setup was created using **create-react-app**. I then ejected the project and modified the webpack config by hand 
to create 2 targets, one to be sent to the browser and another to be rendered by nashorn. That's because nashorn can't handle javascript
modules.
I use **Thymeleaf** as the template engine on the server side and the nashorn rendered html is injected inside it at run time.

This is based on the excellent work by Patrick Grimard (https://github.com/pgrimard/spring-boot-react) and 
Benjamin Winterberg (https://github.com/winterbe/spring-react-example)

## Build steps:
Git clone the repo, cd into the directory and run **mvn compile**
