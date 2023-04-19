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

### Dependencies

Babbage runs independently. However, in order to run it locally in its publishing mode, with the other required services for that, there is a stack that can be used in dp-compose:

[Homepage publishing](https://github.com/ONSdigital/dp-compose/tree/main/v2/stacks#homepage-publishing)

### Configuration

| Environment variable          | Default                | Description
| ------------------------------| -----------------------|-------------------------------------------------------------
| CONTENT_SERVICE_MAX_CONNECTION| 50                     | The maximum number of connections Babbage can make to the content service
| CONTENT_SERVICE_URL           | http://localhost:8082  | The URL to the content service (zebedee)
| ELASTIC_SEARCH_SERVER         | localhost              | The elastic search host and port (The http:// scheme prefix is added programmatically)
| ELASTIC_SEARCH_CLUSTER        |                        | The elastic search cluster
| ENABLE_CACHE                  | N                      | Switch to use (or not) the cache
| ENABLE_COVID19_FEATURE        |                        | Switch to use (or not) the covid feature
| ENABLE_METRICS                | Y                      | Switch to collect (or not) metrics about cache expiry times
| HIGHCHARTS_EXPORT_SERVER      | http://localhost:9999/ | The URL to the highcharts export server
| IS_PUBLISHING                 | N                      | Switch to use (or not) the publishing functionality
| MAP_RENDERER_HOST             | http://localhost:23500 | The URL to the map renderer
| METRICS_PORT                  | 1234                   | The port for the metrics URL
| REDIRECT_SECRET               | secret                 | The code for the redirect
| TABLE_RENDERER_HOST           | http://localhost:23300 | The URL to the table renderer

### Metrics

To see the metrics call the following command while babbage is running:

```bash
curl -s http://localhost:1234/metrics
```

The metrics should look something like this:

```shell
# HELP publish_date_not_present_total Total requests for uris that currently have no publishing date
# TYPE publish_date_not_present_total counter
publish_date_not_present_total 0.0
# HELP publish_date_in_future_total Total requests for uris that have a future publishing date
# TYPE publish_date_in_future_total counter
publish_date_in_future_total 1.0
# HELP publish_date_present_total Total requests for uris that have a past or future publishing date
# TYPE publish_date_present_total counter
publish_date_present_total 0.0
# HELP publish_date_too_far_in_past_total Total requests for uris that have a past publishing date too long ago to concern
# TYPE publish_date_too_far_in_past_total counter
publish_date_too_far_in_past_total 1.0
# HELP cache_expiry_time The time until the cache expires and will be refreshed by another call to the server.
# TYPE cache_expiry_time gauge
# HELP publish_date_in_future_created Total requests for uris that have a future publishing date
# TYPE publish_date_in_future_created gauge
publish_date_in_future_created 1.679675778633E9
# HELP publish_date_not_present_created Total requests for uris that currently have no publishing date
# TYPE publish_date_not_present_created gauge
publish_date_not_present_created 1.679675778633E9
# HELP publish_date_present_created Total requests for uris that have a past or future publishing date
# TYPE publish_date_present_created gauge
publish_date_present_created 1.679675778632E9
# HELP publish_date_too_far_in_past_created Total requests for uris that have a past publishing date too long ago to concern
# TYPE publish_date_too_far_in_past_created gauge
publish_date_too_far_in_past_created 1.679675778633E9
```

### Testing

### Contributing

See [CONTRIBUTING](CONTRIBUTING.md) for details.

### License

Copyright © 2022, Office for National Statistics (https://www.ons.gov.uk)

Released under MIT license, see [LICENSE](LICENSE.md) for details.