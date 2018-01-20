(ns user
  (:require [reloaded.repl :refer [system reset stop]]
           [chatdemo.system]))

(reloaded.repl/set-init! #'chatdemo.system/create-system)