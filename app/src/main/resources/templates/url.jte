@import hexlet.code.models.Url
@import hexlet.code.models.UrlCheck
@import java.util.List
@import java.time.LocalDateTime
@import java.time.format.DateTimeFormatter

@param Url url
@param List<UrlCheck> checks
@param String flash
@param String flashType

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>URL: ${url.getName()}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <h1>Сайт: ${url.getName()}</h1>
        @if (flash != null)
            <div class="alert alert-${flashType}">
                ${flash}
            </div>
        @endif
        <p>ID: ${url.getId()}</p>
        <p>Дата создания: ${url.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}</p>
        <form action="/urls/${url.getId()}/checks" method="post">
            <button type="submit" class="btn btn-primary">Запустить проверку</button>
        </form>
        <h2>Проверки</h2>
        <table class="table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Код ответа</th>
                    <th>Заголовок</th>
                    <th>H1</th>
                    <th>Описание</th>
                    <th>Дата проверки</th>
                </tr>
            </thead>
            <tbody>
                @for (UrlCheck check : checks)
                    <tr>
                        <td>${check.getId()}</td>
                        <td>${check.getStatusCode()}</td>
                        <td>${check.getTitle()}</td>
                        <td>${check.getH1()}</td>
                        <td>${check.getDescription()}</td>
                        <td>${check.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}</td>
                    </tr>
                @endfor
            </tbody>
        </table>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>