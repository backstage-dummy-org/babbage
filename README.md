Babbage
========

Repository for ONS Website Babbage

Babbage is a bespoke service for translating JSON into HTML. The Zebedee-Reader reads .json files off the server for passing to Babbage. Babbage then translates them into the .html files that are used in the website.

Babbage contains two main areas of functionality, as follows:

1. It creates the HTML files for the pages on the website.
2. It creates the HTML files for the website publications in the publishing system [Florence](https://github.com/ONSdigital/florence)

### Getting started

In the babbage repo do one of the following:

* To run Babbage for the website functionality use this command

```shell script
./run.sh
```

* To run Babbage for the publishing functionality use this command

```shell script
./run-publishing.sh
```

#### Dependencies

Babbage runs independently. However, in order to run it locally in its publishing mode, with the other required services for that, there is a stack that can be used in dp-compose:

[Homepage publishing](https://github.com/ONSdigital/dp-compose/tree/main/v2/stacks#homepage-publishing)


