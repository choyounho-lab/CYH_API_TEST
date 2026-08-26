\set ON_ERROR_STOP on

SELECT 'CREATE ROLE cyh_app LOGIN'
WHERE NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'cyh_app')
\gexec

\echo 'Set the password for the cyh_app application user.'
\password cyh_app

SELECT 'CREATE DATABASE cyh_api_test OWNER cyh_app'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'cyh_api_test')
\gexec

\connect cyh_api_test
GRANT ALL ON SCHEMA public TO cyh_app;

\echo 'PostgreSQL initialization completed.'
