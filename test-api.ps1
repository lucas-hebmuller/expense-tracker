Write-Host "========================================" -ForegroundColor Magenta
Write-Host "  Expense Tracker API - Test Suite" -ForegroundColor Magenta
Write-Host "========================================" -ForegroundColor Magenta

$baseUrl = "http://localhost:8080"

# ============================================
# USER API TESTS
# ============================================

Write-Host "`n========== USER API TESTS ==========" -ForegroundColor Magenta

# Test 1: User Not Found (404)
Write-Host "`n--- Test 1: User Not Found (404) ---" -ForegroundColor Cyan
try {
    Invoke-RestMethod -Uri "$baseUrl/api/users/999" -Method Get
    Write-Host "FAIL: Should have returned 404" -ForegroundColor Red
} catch {
    Write-Host "SUCCESS: Got expected 404 error" -ForegroundColor Yellow
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $reader.ReadToEnd() | ConvertFrom-Json | ConvertTo-Json -Depth 5
}

# Test 2: Get All Users (200)
Write-Host "`n--- Test 2: Get All Users (200) ---" -ForegroundColor Cyan
try {
    $users = Invoke-RestMethod -Uri "$baseUrl/api/users" -Method Get
    Write-Host "SUCCESS: Retrieved $($users.Count) users" -ForegroundColor Green
    $users | ConvertTo-Json -Depth 3
} catch {
    Write-Host "FAIL: Could not retrieve users" -ForegroundColor Red
}

# Test 3: Empty Name Validation (400)
Write-Host "`n--- Test 3: Empty Name (400) ---" -ForegroundColor Cyan
$body = @{
    name = ""
    email = "test@example.com"
    password = "password123"
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "$baseUrl/api/users" -Method Post -Body $body -ContentType "application/json"
    Write-Host "FAIL: Should have rejected empty name" -ForegroundColor Red
} catch {
    Write-Host "SUCCESS: Validation rejected empty name" -ForegroundColor Yellow
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $reader.ReadToEnd() | ConvertFrom-Json | ConvertTo-Json -Depth 5
}

# Test 4: Invalid Email (400)
Write-Host "`n--- Test 4: Invalid Email (400) ---" -ForegroundColor Cyan
$body = @{
    name = "John Doe"
    email = "not-an-email"
    password = "password123"
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "$baseUrl/api/users" -Method Post -Body $body -ContentType "application/json"
    Write-Host "FAIL: Should have rejected invalid email" -ForegroundColor Red
} catch {
    Write-Host "SUCCESS: Validation rejected invalid email" -ForegroundColor Yellow
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $reader.ReadToEnd() | ConvertFrom-Json | ConvertTo-Json -Depth 5
}

# Test 5: Short Password (400)
Write-Host "`n--- Test 5: Short Password (400) ---" -ForegroundColor Cyan
$body = @{
    name = "John Doe"
    email = "john@example.com"
    password = "short"
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "$baseUrl/api/users" -Method Post -Body $body -ContentType "application/json"
    Write-Host "FAIL: Should have rejected short password" -ForegroundColor Red
} catch {
    Write-Host "SUCCESS: Validation rejected short password" -ForegroundColor Yellow
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $reader.ReadToEnd() | ConvertFrom-Json | ConvertTo-Json -Depth 5
}

# Test 6: Multiple Validation Errors (400)
Write-Host "`n--- Test 6: Multiple Validation Errors (400) ---" -ForegroundColor Cyan
$body = @{
    name = "A"
    email = "bad-email"
    password = "x"
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "$baseUrl/api/users" -Method Post -Body $body -ContentType "application/json"
    Write-Host "FAIL: Should have rejected multiple errors" -ForegroundColor Red
} catch {
    Write-Host "SUCCESS: Validation caught multiple errors" -ForegroundColor Yellow
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $reader.ReadToEnd() | ConvertFrom-Json | ConvertTo-Json -Depth 5
}

# Test 7: Valid User Creation (201)
Write-Host "`n--- Test 7: Valid User Creation (201) ---" -ForegroundColor Cyan
$randomEmail = "user$(Get-Random)@example.com"
$body = @{
    name = "Test User"
    email = $randomEmail
    password = "password123"
} | ConvertTo-Json

try {
    $newUser = Invoke-RestMethod -Uri "$baseUrl/api/users" -Method Post -Body $body -ContentType "application/json"
    Write-Host "SUCCESS: User created with ID $($newUser.id)" -ForegroundColor Green
    $newUser | ConvertTo-Json -Depth 3
} catch {
    Write-Host "FAIL: Could not create valid user" -ForegroundColor Red
}

# Test 8: Duplicate Email (409)
Write-Host "`n--- Test 8: Duplicate Email (409) ---" -ForegroundColor Cyan
$dupEmail = "duplicate$(Get-Random)@example.com"
$body = @{
    name = "First User"
    email = $dupEmail
    password = "password123"
} | ConvertTo-Json

Write-Host "Creating first user..." -ForegroundColor Gray
try {
    Invoke-RestMethod -Uri "$baseUrl/api/users" -Method Post -Body $body -ContentType "application/json" | Out-Null
    Write-Host "First user created" -ForegroundColor Green
} catch {
    Write-Host "Failed to create first user" -ForegroundColor Red
}

Write-Host "Attempting duplicate..." -ForegroundColor Gray
try {
    Invoke-RestMethod -Uri "$baseUrl/api/users" -Method Post -Body $body -ContentType "application/json"
    Write-Host "FAIL: Should have rejected duplicate email" -ForegroundColor Red
} catch {
    Write-Host "SUCCESS: Duplicate email rejected" -ForegroundColor Yellow
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $reader.ReadToEnd() | ConvertFrom-Json | ConvertTo-Json -Depth 5
}

# ============================================
# CATEGORY API TESTS
# ============================================

Write-Host "`n`n========== CATEGORY API TESTS ==========" -ForegroundColor Magenta

# Test 9: Category Not Found (404)
Write-Host "`n--- Test 9: Category Not Found (404) ---" -ForegroundColor Cyan
try {
    Invoke-RestMethod -Uri "$baseUrl/api/categories/999" -Method Get
    Write-Host "FAIL: Should have returned 404" -ForegroundColor Red
} catch {
    Write-Host "SUCCESS: Got expected 404 error" -ForegroundColor Yellow
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $reader.ReadToEnd() | ConvertFrom-Json | ConvertTo-Json -Depth 5
}

# Test 10: Get All Categories (200)
Write-Host "`n--- Test 10: Get All Categories (200) ---" -ForegroundColor Cyan
try {
    $categories = Invoke-RestMethod -Uri "$baseUrl/api/categories" -Method Get
    Write-Host "SUCCESS: Retrieved $($categories.Count) categories" -ForegroundColor Green
    $categories | ConvertTo-Json -Depth 3
} catch {
    Write-Host "FAIL: Could not retrieve categories" -ForegroundColor Red
}

# Test 11: Empty Category Name (400)
Write-Host "`n--- Test 11: Empty Category Name (400) ---" -ForegroundColor Cyan
$body = @{
    name = ""
    user = @{ id = 5 }
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "$baseUrl/api/categories" -Method Post -Body $body -ContentType "application/json"
    Write-Host "FAIL: Should have rejected empty name" -ForegroundColor Red
} catch {
    Write-Host "SUCCESS: Validation rejected empty name" -ForegroundColor Yellow
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $reader.ReadToEnd() | ConvertFrom-Json | ConvertTo-Json -Depth 5
}

# Test 12: Valid Category Creation (201)
Write-Host "`n--- Test 12: Valid Category Creation (201) ---" -ForegroundColor Cyan
$body = @{
    name = "Test Category $(Get-Random)"
    user = @{ id = 5 }
} | ConvertTo-Json

try {
    $newCategory = Invoke-RestMethod -Uri "$baseUrl/api/categories" -Method Post -Body $body -ContentType "application/json"
    Write-Host "SUCCESS: Category created with ID $($newCategory.id)" -ForegroundColor Green
    $newCategory | ConvertTo-Json -Depth 3
} catch {
    Write-Host "FAIL: Could not create valid category" -ForegroundColor Red
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $reader.ReadToEnd()
}

# ============================================
# TRANSACTION API TESTS
# ============================================

Write-Host "`n`n========== TRANSACTION API TESTS ==========" -ForegroundColor Magenta

# Test 13: Transaction Not Found (404)
Write-Host "`n--- Test 13: Transaction Not Found (404) ---" -ForegroundColor Cyan
try {
    Invoke-RestMethod -Uri "$baseUrl/api/transactions/999" -Method Get
    Write-Host "FAIL: Should have returned 404" -ForegroundColor Red
} catch {
    Write-Host "SUCCESS: Got expected 404 error" -ForegroundColor Yellow
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $reader.ReadToEnd() | ConvertFrom-Json | ConvertTo-Json -Depth 5
}

# Test 14: Get All Transactions (200)
Write-Host "`n--- Test 14: Get All Transactions (200) ---" -ForegroundColor Cyan
try {
    $transactions = Invoke-RestMethod -Uri "$baseUrl/api/transactions" -Method Get
    Write-Host "SUCCESS: Retrieved $($transactions.Count) transactions" -ForegroundColor Green
    $transactions | ConvertTo-Json -Depth 3
} catch {
    Write-Host "FAIL: Could not retrieve transactions" -ForegroundColor Red
}

# Test 15: Missing Amount (400)
Write-Host "`n--- Test 15: Missing Amount (400) ---" -ForegroundColor Cyan
$body = @{
    description = "Test"
    transactionDate = "2025-12-20"
    user = @{ id = 5 }
    category = @{ id = 6 }
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "$baseUrl/api/transactions" -Method Post -Body $body -ContentType "application/json"
    Write-Host "FAIL: Should have rejected missing totalAmount" -ForegroundColor Red
} catch {
    Write-Host "SUCCESS: Validation caught missing totalAmount" -ForegroundColor Yellow
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $reader.ReadToEnd() | ConvertFrom-Json | ConvertTo-Json -Depth 5
}

# Test 16: Future Date (400)
Write-Host "`n--- Test 16: Future Date (400) ---" -ForegroundColor Cyan
$body = @{
    description = "Future expense"
    totalAmount = "100.00"
    transactionDate = "2026-12-31"
    user = @{ id = 5 }
    category = @{ id = 6 }
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "$baseUrl/api/transactions" -Method Post -Body $body -ContentType "application/json"
    Write-Host "FAIL: Should have rejected future date" -ForegroundColor Red
} catch {
    Write-Host "SUCCESS: Validation rejected future date" -ForegroundColor Yellow
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $reader.ReadToEnd() | ConvertFrom-Json | ConvertTo-Json -Depth 5
}

# Test 17: Valid Transaction (201)
Write-Host "`n--- Test 17: Valid Transaction (201) ---" -ForegroundColor Cyan
$body = @{
    description = "Test transaction"
    totalAmount = "50.00"
    transactionDate = "2025-12-20"
    user = @{ id = 5 }
    category = @{ id = 6 }
} | ConvertTo-Json

try {
    $newTransaction = Invoke-RestMethod -Uri "$baseUrl/api/transactions" -Method Post -Body $body -ContentType "application/json"
    Write-Host "SUCCESS: Transaction created with ID $($newTransaction.id)" -ForegroundColor Green
    $newTransaction | ConvertTo-Json -Depth 3
} catch {
    Write-Host "FAIL: Could not create valid transaction" -ForegroundColor Red
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $reader.ReadToEnd()
}

# ============================================
# SUMMARY
# ============================================

Write-Host "`n`n========================================" -ForegroundColor Magenta
Write-Host "  All Tests Complete!" -ForegroundColor Magenta
Write-Host "========================================" -ForegroundColor Magenta
Write-Host "`nGreen = Success" -ForegroundColor Green
Write-Host "Yellow = Expected error (validation working)" -ForegroundColor Yellow
Write-Host "Red = Test failed" -ForegroundColor Red