@import java.util.List
@import hexlet.code.models.Url
@import java.time.format.DateTimeFormatter

@param List<Url> urls
@param String flash
@param String flashType

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>URL List</title>
    <style>
        .alert-success { color: green; }
        .alert-danger { color: red; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid black; padding: 8px; text-align: left; }
    </style>
</head>
<body>
    @if(flash != null)
        <div class="alert alert-${flashType}">
            ${flash}
        </div>
    @endif

    <h1>URLs</h1>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Created At</th>
            </tr>
        </thead>
        <tbody>
            @for(Url url : urls)
                <tr>
                    <td>${url.getId()}</td>
                    <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                    <td>${url.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}</td>
                </tr>
            @endfor
        </tbody>
    </table>
</body>
</html>