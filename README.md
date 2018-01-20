# chatdemo

A demo of multiuser chat

## Overview

Multi-user chat using
* clojure and clojurescript
* http-kit
* chord
* async - chan and mult to pus to all connected clients

## Setup

To get an interactive development environment run:

    lein repl
    chatdemo.system=> (ns user)
    user=>(reset)
    :reloading (chatdemo.web chatdemo.system user)
    Server started on http://localhost:9009
    :resumed

This will start the web server on port 9009 and allow you to stop (stop) and restart (reset)
the web server. Then, in a new command prompt run

    lein figwheel

Now you can browse to localhost:9009 to see the app running

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `localhost:9009`. You will not
get live reloading, nor a REPL. 

## License


Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
