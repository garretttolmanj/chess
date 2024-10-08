# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
## Phase 2 Chess Design
[https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9MKV9fQA0MH246snQHOPlU7N9KMPASAirc30AvpjCJTAFrOxclOX9g1AjYxMHGwuqS1Arz+tzWzt7EymxzYnG4sAup1E5SgURimSgAApItFYpRIgBHHxqMAAShOxVE50KkJU5SowGQWkySNhqKgGKxsTxJMMF1k8iUKnU5XsKDAAFUOgj7o88ezFMo1KoiUYdKUAGJITgwQWUcUwHRhEXAUaYcWcqXnCEE0kwNA+BAIfEiFQy-WS7kgGFyFCqxH3cVi7QG9Qy4ylBQcDgqjri61UQls70O1SlJ0oF0KHxgVII4DJ1KevXRrnSi7+wPBpMpsMso2FEHXMoRNQWrCVsHlorUG4wO4dR77cYzObplP1CAAa3QXbmx1OlCb2XM5QATE4nN120MdU81nMNn3UgPh2hR0czJxTF5fP4AtB2LyYAAZCDRJIBNIZLLIcwyifV6p1JqtAzqBJoEuAwdquXY9vM+jvMsYHjsUk4XA2rbLg8oHfOBbwfF8awHoh4LEiaKDlAgd5Kgit73gy2LMgR74EWSFJIFSKBkSRaCUUy4aRoU9q5jyfJusKIGjF6HIxn6cowIqypuuqmowNquo8YauTGjahEhmq2icbaqkRqaW7mIQLoCRhyyzDJ2jUWpdo5lK5TxomGZphmWZKb6+YSYWMDFpmWllipFZXGC5SRKodaYLhTYfrccwKWuLy9hmO4jthMCwS24IXNOGBzguS6xUJ8U-H0W7JXuqXHOgHDHt4fiBF4KDoDed6+Mwj7pJkmDZcwumthU0gAKLXgN9QDc0LR-qoAHdKVQ7oOG8GBaCrazbuEVBZQulQjAxH2K1LF7cm7G4tprL4Wp9GUi6B2tcdVl6WdMi2dyRgoNwmQ+c5-ZzWgIkSrm4nlNIb18oYPmyWEq3zf5CEbdWoXhZFAXNiUMXpWcyPdbli69Glh7VZ4tVnjCwbXnCMAAOKrtK7XPl1r55OdqOVBTI3jfYq4zUlP0LXhlzLdWUNoOtAu0RdO1wlToyqGRkvU3dp1iw9l2MddZOxFLagK-53HPbGMC8mAn1C39Pp5oU-qU3y3kuVpbl5ltprg35NGO+pJRIAAZpYxvc7uszO-I91cU9om8RLGvU1924-abYkeeUFPW8gsQwBAnsG9TivI7hIW1gg9Zw1FcHVj0cwc9L+wVP0FcoAAktI+wAIyzgAzAALBsT6ZG6nZoRsOgIKAg696h2EbLXAByY8HIcMCNOj8FZQzYDY-lfS16oVc16uDfN23ndzN3rqFWBA9DyAI+n-35ertPowwfPlVHoTp6BNgPhQNg3DwM6mSU6uFIHUXw5B6kzPqtQGjs05sEP26AgJTxnkCXmMpc4wCFggu+oFyjAiLm7eyf8UCawRHAQhmttauyZttckV1qSkITJkchKBMRUWzrrMOdkDZ8l9t9XcccAYJytswQOwBswcPcvgm2JYXbWUkR7b2PCY7+ykb5IObDQ7-U4Q5Rhq4ES11cnrQG3kgwAN1DDJaVY85hQLiLKsxcMoxQ3rvRuzwW4dzxh+KcK8164ycaMPeriD54yqjVN+ARLBvWIskGAAApCASpTGGACIPYe9NQFKwgfyH8LRa5c14fA3o39gARKgHACAxEoCzFrg3RefM0EYMKUPEpZSKlVOcbg0WkiABW8S0DELiUqJhLCOI6xRtQhiTEEQDL6auChsj2GaJeobRRZV+HKQthJJOwjbbyDEYsh2VCnY7NEaMlkZJKBex9k5IWAdjnBx0gss2fEwDEOqdINZ7kNmJ2tprDUYQ3l7LNvY5WmdRilkoRop58lsBMWIX4JiHBNYGPEebWU5R+QwpdIk7OsMBZWMRnDZG0U2weJLl40BPiy7PwJieOqAQvDFPgNwPA6DsDfyMgQRIQC6bdQyZ+Qaw1RrjWMCg3FljbGNkkSAZlUAFBsuQCAeIiQSEyrmQ9JW4zaHMUQAmekzDGQnVGfbeyMqEQfNRZbHVLKqS6H0GIcx-NLE1msYXTpvVS6koyuSmcMB5w4ypfjTAQA](https://sequencediagram.org/index.html?presentationMode=readOnly&shrinkToFit=true#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9MKV9fQA0MH246snQHOPlU7N9KMPASAirc30AvpjCJTAFrOxclOX9g1AjYxMHGwuqS1Arz+tzWzt7EymxzYnG4sAup1E5SgURimSgAApItFYpRIgBHHxqMAAShOxVE50KsnkShU6nK9hQYAAqh0EfdHniSYplGpVESjDpSgAxJCcGB0ygsmA6MKM4CjTAssns84QgkqcpoHwIBD4kQqTkytkUkAwuQoIWI+4s5naWXqTnGUoKDgcQUdFkaqiEi468mqUr6lCGhQ+MCpBHAAOpM3Si26jkXG12h3+wPOyFa3IXEHXMoRNSqrDpsHywqnG4wO4dR77cYzOYhwP1CAAa3QFbmxyL4Iu2XM5QATE4nN1S0NJU81nMNjXUnXG2hm0czJxTF5fP4AtB2FSYAAZCDRJIBNIZLLIcyctvlap1JqtAzqBJoAcDMvDitV+b6d7LF+t4qUTl54uDg8z7fK+bwfF8axzv+4KFoqKDlAgO78gi267hiWKxHiyaGO6kaepS1LGgyT6jOapJRta3IwHyArGiKYowBKUoenKqawZq8GOsK2gum6xJ4ey5Q+n6obBqG4YsVaMZUXGMAJmGPHYQWlygsWkSqDmmDQcpZ4lnMTEji81ahlOTaQTA37UL+bHwMeGA9n2A76SRhk-H0E6mTO5nHOgHCLt4fiBF4KDoFuO6+Mw+7pJkmCdnk7ElOe0gAKKbsl9TJc0LQ3qod7dB5DboC61mFNB5QFdOWlXGCCocQh4UBihDVgOh2JYXB2oCRSMBUmA8libWhVoGRrKepR5QAOLUnJ4k8ZJ0a1a6SozYmikdbh5H4UYKDcJk-UVegI2WtGhQ2jIO3UoY8n0WEB1oLxKZptVanZggubPe2CUARZxWfbZORgA5-a9D9vn+cugQwg6m5wjAE3DhyUWHrFdnMItxYVBN6VZfYw75SZQ2-X+H3lQTlXaYtUIwMgsTw6MqgoXCdNqK1mEPTh-GbYJPXUvtZOHRGXNSadVFTcw11zV1C0JVTEvyOznVC161NMwjA2TkNR0UdJk3TTTzAQAAZj1CMKzZZVZhpb1Vapf26T0cy4-T+wVP0TsoAAktI+wAIzdgAzAALBsB6ZMa5YgRsOgIKA9bh8BkEbO7AByCcHIcMCNJZZw2XFgMwL2wMO307uqC7bvDl7vsB8Hcyh0aLkvlHMcgHHjeR47w6p6MX6Zz5C6eAFK7YD4UDYNw8AGpkcPDik0VHgDp4-pmF4NDjePBPz969CXXdp0CRNPbbpODdOD4p8+5TAh9S91TAwmZMzCJwFPKDM6zuJm5zo3c71fOnwLea404bTTlsAQWP8pKU2WmAr+MgpZCVfk-d2EkpbANkszM2R8MzlHUppCm6NMzF3dlXZ4fsg4-TbMpPOQMnK71GKQ8o5DA6gwHkuQKARLA7UQskGAAApCA-IZ6jECNHWOKNF6EPPFUGkV4Wju3xgA7ePRx7AC4VAOAEBEJQFmCQ6Q2cSoqRwTAO6A5VHqM0do3Rld9E2wzLfJanEABWgi0BPwEfyd+KBMRtTgfNAifVRJ3S1mNHWIDxazXlkAmy2FyiwL8QgnmYBkE2JCaxEWutmDM1FGEPREDjo6TgpSYcSZ1rf2OuUPwWhH7DgZNgapKAODM1QUrYBNJ6mGmEWIJS5sSaW3wR9GJy9biUOXtQ1GtCQb9z8oPCGAQvBqNsr6WAwBsDj0IPERIc9kZ5wcRjFKaUMpZWMIfUqJM7H5mgZxBJSshLcDwMaBmaThZcnKIgZZXEoAaB6eUqMdzlnMyefk7WGSll4C6d8sp8Dbn33uVAeSnkgXRNBe88F8lIDTkhRxZSFs8HWwIV9IhoyrJ-RoQXRyUz5x+SAA)
