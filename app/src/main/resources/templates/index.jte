@template()
@param String flash
@param String flashType
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>URL Analyzer</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-5">
    <h1 class="text-primary">URL Analyzer 🚀</h1>

    <!-- Flash-сообщения -->
    @if(flash != null)
        <div class="alert @if(flashType.equals("success"))alert-success@else alert-danger@endif alert-dismissible fade show" role="alert">
            ${flash}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    @endif

    <!-- Форма для ввода URL -->
    <form action="/urls" method="post">
        <div class="mb-3">
            <label for="url" class="form-label">Enter URL</label>
            <input type="text" class="form-control" id="url" name="url" placeholder="https://example.com" required>
        </div>
        <button type="submit" class="btn btn-primary">Analyze</button>
    </form>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>