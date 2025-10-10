<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>URLs</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <h1>URLs</h1>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Last Check</th>
                    <th>Status Code</th>
                </tr>
            </thead>
            <tbody>
                @for (url in urls)
                    <tr>
                        <td>${url.getId()}</td>
                        <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                        <td>
                            @if (latestChecks.get(url.getId()) != null)
                                ${latestChecks.get(url.getId()).getCreatedAt()}
                            @else
                                -
                            @endif
                        </td>
                        <td>
                            @if (latestChecks.get(url.getId()) != null)
                                ${latestChecks.get(url.getId()).getStatusCode()}
                            @else
                                -
                            @endif
                        </td>
                    </tr>
                @endfor
            </tbody>
        </table>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>