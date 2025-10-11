@import hexlet.code.models.Url
@import java.util.List

@param String flash
@param String flashType

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Анализатор страниц</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <h1>Анализатор страниц</h1>
        @if (flash != null)
            <div class="alert alert-${flashType}">
                ${flash}
            </div>
        @endif
        <form action="/urls" method="post">
            <div class="mb-3">
                <label for="url" class="form-label">URL</label>
                <input type="text" class="form-control" id="url" name="url" placeholder="https://example.com">
            </div>
            <button type="submit" class="btn btn-primary">Проверить</button>
        </form>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>