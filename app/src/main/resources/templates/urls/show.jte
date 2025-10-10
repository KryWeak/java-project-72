<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>URL Details</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <h1>URL: ${url.getName()}</h1>
        @if (ctx.flash() != null)
            <div class="alert alert-${ctx.flashType()} alert-dismissible fade show" role="alert">
                ${ctx.flash()}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        @endif
        <p>ID: ${url.getId()}</p>
        <p>Created At: ${url.getCreatedAt()}</p>
        <form method="post" action="/urls/${url.getId()}/checks">
            <button type="submit" class="btn btn-primary">Run Check</button>
        </form>
        <h2 class="mt-4">Checks</h2>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Status Code</th>
                    <th>Title</th>
                    <th>H1</th>
                    <th>Description</th>
                    <th>Created At</th>
                </tr>
            </thead>
            <tbody>
                @for (check in checks)
                    <tr>
                        <td>${check.getId()}</td>
                        <td>${check.getStatusCode()}</td>
                        <td>${check.getTitle() != null ? check.getTitle() : "-"}</td>
                        <td>${check.getH1() != null ? check.getH1() : "-"}</td>
                        <td>${check.getDescription() != null ? check.getDescription() : "-"}</td>
                        <td>${check.getCreatedAt()}</td>
                    </tr>
                @endfor
            </tbody>
        </table>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>