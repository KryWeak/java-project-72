<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>URL Analyzer</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <h1>URL Analyzer</h1>
        @if (ctx.flash() != null)
            <div class="alert alert-${ctx.flashType()} alert-dismissible fade show" role="alert">
                ${ctx.flash()}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        @endif
        <form method="post" action="/urls" class="mt-3">
            <div class="mb-3">
                <label for="url" class="form-label">Enter URL</label>
                <input type="text" class="form-control" id="url" name="url" required>
            </div>
            <button type="submit" class="btn btn-primary">Add URL</button>
        </form>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>