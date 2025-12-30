<template>
  <body>

<div class="register-shell">

    <div class="logo-title">
        <div class="logo-img">
             <img th:src="@{/img/catbox.png}" alt="Logo">
        </div>

        <h1>家庭物料管理系統</h1>
        <p>註冊後即可開始管理您的物品</p>
    </div>

    <div class="container">
        <h2>註冊帳號</h2>
        <div class="subtitle">請填寫您的基本資訊</div>

        <div class="error" th:if="${error}" th:text="${error}"></div>

        <form th:action="@{/register}" method="post" th:object="${user}">

            <div class="field-group">
                <label for="username">帳號</label>
                <input id="username" type="text" th:field="*{username}" placeholder="請輸入帳號">
            </div>

            <div class="field-group">
                <label for="password">密碼</label>
                <input id="password" type="password" th:field="*{password}" placeholder="請輸入密碼">
            </div>

            <button type="submit">註冊</button>
        </form>

        <div class="link">
            已經有帳號了？<a href="/login">返回登入</a>
        </div>
    </div>

</div>

</body>
</template>
