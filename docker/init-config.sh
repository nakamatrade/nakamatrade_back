#!/bin/bash
echo "host all all all scram-sha-256" >> "$PGDATA/pg_hba.conf"
echo "listen_addresses = '*'" >> "$PGDATA/postgresql.conf"