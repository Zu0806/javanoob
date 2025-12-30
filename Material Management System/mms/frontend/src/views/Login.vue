<template>
  <body>
<div class="login-shell">

    <div class="logo-title">
        <div class="logo-img">
    <img th:src="@{/img/catbox.png}" alt="Logo">
</div>

        <h1>家庭物料管理系統</h1>
        <p>登入後即可管理家中的物品與庫存</p>
    </div>

    <div class="container">
        <h2>登入系統</h2>
        <div class="subtitle">請輸入您的帳號與密碼</div>

        <div class="error" th:if="${error}" th:text="${error}"></div>

        <form th:action="@{/login}" method="post" th:object="${loginRequest}">
            <div class="field-group">
                <label for="username">帳號</label>
                <input id="username" type="text" th:field="*{username}" placeholder="請輸入帳號">
            </div>

            <div class="field-group">
                <label for="password">密碼</label>
                <input id="password" type="password" th:field="*{password}" placeholder="請輸入密碼">
            </div>

            <div class="helper-row">
                <label class="remember">
                    <input type="checkbox" name="remember-me">
                    <span>記住我</span>
                </label>

            </div>

            <button type="submit">登入</button>
        </form>

        <div class="link">
            還沒有帳號？<a href="/register">點我註冊</a>
        </div>
    </div>
</div>
</body>
</template>
