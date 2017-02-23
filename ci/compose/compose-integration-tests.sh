#!/bin/bash
pushd /babbage
ls -lR
printf  "waiting for ElasticSearch"
COUNTER=0
until [ ${COUNTER} -lt 20 ] || [ $(curl --output /dev/null --silent --head --fail http://elastic:9200) ]; do
    printf '.'
    COUNTER=${COUNTER}+1
    sleep 5
done

export ELASTIC_SEARCH_SERVER=elastic
mvn clean test-compile surefire:test@integration-test
mavenReturnCode=$?
popd
exit ${mavenReturnCode}
