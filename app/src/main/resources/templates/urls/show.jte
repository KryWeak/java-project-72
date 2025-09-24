@template()
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>URL Details</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-5">
    <h1>Сайт</h1>
    <a href="/urls" class="btn btn-primary mb-3">Назад к списку</a>
    <dl>
        <dt>ID</dt>
        <dd>${url.getId()}</dd>
        <dt>Имя</dt>
        <dd>${url.getName()}</dd>
        <dt>Дата создания</dt>
        <dd>${url.getCreatedAt()}</dd>
    </dl>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>