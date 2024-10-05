# Design Overview

Initial design, take in messages of key->value, where value is a number

Allow subscribers to get min/mean/max stats sent out anytime it changes. For testing, we'll use the 1brc format of messages of city names and temperatures:

> The text file contains temperature values for a range of weather stations. Each row is one measurement in the format <string: station name>;<double: measurement>, with the measurement value having exactly one fractional digit.

```
Hamburg;12.0
Bulawayo;8.9
Palembang;38.8
St. John's;15.2
Cracow;12.6
Bridgetown;26.9
Istanbul;6.2
Roseau;34.4
Conakry;31.2
Istanbul;23.0
```

For us, one message is one row. So, we'll accept text in the format `<string: station name>;<double: measurement>`

## Goals

- Superfast
- Many subscribers
- Many publishers

## Secondary Goals (not ones to focus on yet)

- Distributed

## Ideas of the Design

One server, using Java NIO that accepts connections and works out if they're subscribers or publishers.
For publishers, listen for messages - pass to a worker thread (via some mechanism, more soon)
For subscribers, maintain a list of them...

Have a thread keeping state, somehow. Initially, can do this without being superfast. Optimise later. No locks to start though.

So flow for one message.. read message into buffer. Pass to thread to update state, work out new stats. Then, notify subscribers of any changes.