#!/bin/bash
pushd /babbage
printf  "waiting for ElasticSearch"
COUNTER=0
until [ $COUNTER -gt 20 ] || [ $(curl --output /dev/null --silent --head --fail http://elastic:9200) ]; do
    printf '.'
    sleep 5

done


export ELASTIC_SEARCH_SERVER=elastic
mvn clean test-compile surefire:test@integration-test

popd