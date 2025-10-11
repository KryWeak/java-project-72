@import hexlet.code.models.Url
@import hexlet.code.models.UrlCheck
@import java.util.List
@import java.util.Map
@import java.time.format.DateTimeFormatter

@param List<Url> urls
@param Map<Long, List<UrlCheck>> checksByUrl
@param String flash
@param String flashType

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Список URL</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <h1>Сайты</h1>
        @if (flash != null)
            <div class="alert alert-${flashType}">
                ${flash}
            </div>
        @endif
        <table class="table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Имя</th>
                    <th>Дата последней проверки</th>
                    <th>Код ответа</th>
                </tr>
            </thead>
            <tbody>
                @for (Url url : urls)
                    <tr>
                        <td>${url.getId()}</td>
                        <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                        <td>
                            @if (checksByUrl.get(url.getId()) != null && !checksByUrl.get(url.getId()).isEmpty())
                                ${checksByUrl.get(url.getId()).get(0).getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}
                            @else
                                -
                            @endif
                        </td>
                        <td>
                            @if (checksByUrl.get(url.getId()) != null && !checksByUrl.get(url.getId()).isEmpty())
                                ${checksByUrl.get(url.getId()).get(0).getStatusCode()}
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