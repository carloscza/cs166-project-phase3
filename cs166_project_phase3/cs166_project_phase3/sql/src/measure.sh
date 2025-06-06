#!/bin/bash
#psql -h localhost -p $PGPORT $DB_NAME < create_tables.sql > /dev/null
#sleep 5

echo "Query time without indexes"
cat <(echo '\timing') queries.sql | psql -h localhost -p $PGPORT $USER'_project_phase_3_DB' | grep Time | awk -F "Time" '{print "Query" FNR $2;}'

psql -h localhost -p $PGPORT $USER'_project_phase_3_DB' < create_indexes.sql > /dev/null

echo "Query time with indexes"
cat <(echo '\timing') queries.sql |psql -h localhost -p $PGPORT $USER'_project_phase_3_DB' | grep Time | awk -F "Time" '{print "Query" FNR $2;}'