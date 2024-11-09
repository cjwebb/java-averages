# Java Averages

A fun side-project to play with various coding techniques, with a view on performance... and how to get to good performance.

## TODO
- rework Server so that message parsing, storing, send/receive is separated (so I can test it)
- implement (with tests) the min/max/mean logic in Server.
  - integration tests need more cases
  - can this be unit tested?

Then,
- perf-test measurements.
- make fast

## Reading

These two links describe using Java NIO to re-use the same thread to handle multiple clients:

- https://www.machinet.net/tutorial-eng/how-to-implement-sockets-in-java-applications
- https://www.baeldung.com/java-nio-selector

Should probably start by explaining these concepts when I write this up.

And this is about `xrank`, what Q/KDB uses to compute quartiles / percentiles:

- https://code.kx.com/q4m3/A_Built-in_Functions/#a112-xrank
