@param String flash
@param String flashType

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>URL Shortener</title>
    <style>
        .alert-success { color: green; }
        .alert-danger { color: red; }
    </style>
</head>
<body>
    @if(flash != null)
        <div class="alert alert-${flashType}">
            ${flash}
        </div>
    @endif

    <h1>Add URL</h1>
    <form action="/urls" method="post">
        <label for="url">URL:</label>
        <input type="text" id="url" name="url" placeholder="https://example.com" required>
        <button type="submit">Submit</button>
    </form>
</body>
</html>