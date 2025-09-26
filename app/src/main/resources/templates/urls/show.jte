@import hexlet.code.models.Url
@import java.time.format.DateTimeFormatter

@param Url url

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>URL Details</title>
    <style>
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid black; padding: 8px; text-align: left; }
    </style>
</head>
<body>
    <h1>URL Details</h1>
    <table>
        <tr>
            <th>ID</th>
            <td>${url.getId()}</td>
        </tr>
        <tr>
            <th>Name</th>
            <td>${url.getName()}</td>
        </tr>
        <tr>
            <th>Created At</th>
            <td>${url.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}</td>
        </tr>
    </table>
    <a href="/urls">Back to URL List</a>
</body>
</html>