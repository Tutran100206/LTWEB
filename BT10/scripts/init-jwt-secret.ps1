$ErrorActionPreference = 'Stop'
$secretPath = Join-Path (Split-Path $PSScriptRoot -Parent) '.jwt-local.properties'
if (Test-Path -LiteralPath $secretPath) {
    Write-Output 'Local JWT configuration already exists; kept unchanged.'
    exit 0
}
$secretBytes = New-Object byte[] 32
$secretGenerator = [System.Security.Cryptography.RandomNumberGenerator]::Create()
try { $secretGenerator.GetBytes($secretBytes) } finally { $secretGenerator.Dispose() }
$secretLine = 'security.jwt.secret-key=${JWT_SECRET_KEY:' + [Convert]::ToBase64String($secretBytes) + '}'
[IO.File]::WriteAllText($secretPath, $secretLine + [Environment]::NewLine)
Write-Output 'Created .jwt-local.properties with a random 256-bit key (excluded from Git).'
