param(
    [string]$HostName = 'localhost',
    [int]$Port = 5432,
    [string]$AdminUser = 'postgres'
)

$ErrorActionPreference = 'Stop'

$psqlCommand = Get-Command psql -ErrorAction SilentlyContinue
if ($psqlCommand) {
    $psqlPath = $psqlCommand.Source
} else {
    $psqlPath = Get-ChildItem -Path 'C:\Program Files\PostgreSQL\*\bin\psql.exe' -ErrorAction SilentlyContinue |
        Sort-Object FullName -Descending |
        Select-Object -First 1 -ExpandProperty FullName
}

if (-not $psqlPath) {
    throw 'psql was not found. Install PostgreSQL or add its bin directory to PATH.'
}

$sqlFile = Join-Path $PSScriptRoot '..\database\init-postgres.sql'

Write-Host "Using: $psqlPath"
Write-Host 'Enter the PostgreSQL administrator password when prompted.'
Write-Host 'For the cyh_app password, enter the same DB_PASSWORD value stored in .env.'

& $psqlPath -h $HostName -p $Port -U $AdminUser -d postgres -W -f $sqlFile
if ($LASTEXITCODE -ne 0) {
    throw "PostgreSQL initialization failed with exit code $LASTEXITCODE."
}

Write-Host 'Database cyh_api_test and user cyh_app are ready.'
