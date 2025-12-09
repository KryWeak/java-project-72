@import hexlet.code.dto.UrlPage
@import hexlet.code.utils.BeautyTime
@param UrlPage page


@template.layout.page(
content = @`
    <main class="flex-grow-1">
        @if(page.getFlash() != null)
            @if(page.getFlash().equals("Некоректный адрес"))
                <div class="rounded-0 m-0 alert alert-dismissible fade show alert-danger" role="alert">
                    <p class="m-0">${page.getFlash()}</p>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            @else
                <div class="rounded-0 m-0 alert alert-dismissible fade show alert-success" role="alert">
                    <p class="m-0">${page.getFlash()}</p>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            @endif
        @endif

        <section>

            <div class="container-lg mt-5">
                <h1>Сайт: ${page.getUrl().getName()}</h1>

                <table class="table table-bordered table-hover mt-3">
                    <tbody>
                    <tr>
                        <td>ID</td>
                        <td>${page.getUrl().getId()}</td>
                    </tr>
                    <tr>
                        <td>Имя</td>
                        <td>${page.getUrl().getName()}</td>
                    </tr>
                    <tr>
                        <td>Дата создания</td>
                        <td>${BeautyTime.getBeautyTime(page.getUrl().getCreatedAt())}</td>
                    </tr>
                    </tbody>
                </table>

                <h2 class="mt-5">Проверки</h2>
                <form method="post" action="/urls/${page.getUrl().getId()}/checks">
                    <button type="submit" class="btn btn-primary">Запустить проверку</button>
                </form>

                <table class="table table-bordered table-hover mt-3">
                    <thead>
                    <th class="col-1">ID</th>
                    <th class="col-1">Код ответа</th>
                    <th>title</th>
                    <th>h1</th>
                    <th>description</th>
                    <th class="col-2">Дата проверки</th>
                    </thead>
                    <tbody>
                    @if(!page.getUrl().getUrlChecks().isEmpty())
                        @for(var urlCheck: page.getUrl().getUrlChecks())
                            <tr>
                                <td>
                                    ${urlCheck.getId()}

                                </td>
                                <td>
                                    ${urlCheck.getStatusCode()}
                                </td>
                                <td>
                                    ${urlCheck.getTitle()}
                                </td>
                                <td>
                                    ${urlCheck.getH1()}
                                </td>
                                <td>
                                    ${urlCheck.getDescription()}
                                </td>
                                <td>
                                    ${BeautyTime.getBeautyTime(urlCheck.getCreatedAt())}
                                </td>
                            </tr>
                        @endfor
                    @endif
                    </tbody>
                </table>
            </div>

        </section>
    </main>

`
)