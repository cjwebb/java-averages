# Java Averages

A fun side-project to play with various coding techniques, with a view on performance... and how to get to good performance.

## TODO

- enable GC logs
- see that compilation in hotspot has occurred.
- write something up. compare transports for speed. probs need mean/max/mode.

- Then, aim at taking a query from client and calculating averages as we go. speed! subscribe to updates on it.

## Sources

A sample echo client:

https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/networking/sockets/examples/EchoClient.java



## Reading

These two links describe using Java NIO to re-use the same thread to handle multiple clients:

- https://www.machinet.net/tutorial-eng/how-to-implement-sockets-in-java-applications
- https://www.baeldung.com/java-nio-selector

And this is about `xrank`, what Q/KDB uses to compute quartiles / percentiles:

- https://code.kx.com/q4m3/A_Built-in_Functions/#a112-xrank
