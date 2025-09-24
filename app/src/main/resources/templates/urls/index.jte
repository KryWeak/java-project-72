@template()
@param List<hexlet.code.models.Url> urls
@param String flash
@param String flashType
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>URLs</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-5">
    <h1>Сайты</h1>
    <a href="/" class="btn btn-primary mb-3">Добавить URL</a>
    @if(flash != null)
        <div class="alert @if(flashType == 'success')alert-success@else alert-danger@endif alert-dismissible fade show" role="alert">
            ${flash}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    @endif
    <table class="table">
        <thead>
            <tr>
                <th>ID</th>
                <th>Имя</th>
                <th>Дата создания</th>
            </tr>
        </thead>
        <tbody>
            @for(url in urls)
                <tr>
                    <td>${url.getId()}</td>
                    <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                    <td>${url.getCreatedAt()}</td>
                </tr>
            @endfor
        </tbody>
    </table>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>