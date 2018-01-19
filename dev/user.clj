(ns user
  (:require [reloaded.repl :refer [system reset stop]]
           [solo.system]))

(reloaded.repl/set-init! #'solo.system/create-system)