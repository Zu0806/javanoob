<template>
  <div class="container">
    <div class="header-row">
      <div>
        <div class="page-title">📝 記事本總覽</div>
        <div style="color: var(--muted); font-size: 13px; margin-top: 4px;">
          查看所有家庭成員的留言與代辦事項
        </div>
      </div>

      <RouterLink to="/items" class="btn btn-outline">
        &larr; 回到首頁
      </RouterLink>
    </div>

    <form action="/notes" method="post" style="display: flex; gap: 8px; margin-bottom: 24px;">
      <input
        type="text"
        name="text"
        required
        placeholder="在此輸入新的記事..."
        style="flex: 1; padding: 10px 14px; border: 1px solid var(--border); border-radius: 12px; outline: none;"
      />
      <button type="submit" class="btn btn-primary">新增</button>
    </form>

    <div style="overflow-x: auto;">
      <table>
        <thead>
          <tr>
            <th class="hide-mobile" width="5%">ID</th>
            <th width="45%">內容事項</th>
            <th width="15%">留言者</th>
            <th width="20%">時間</th>
            <th width="15%">狀態</th>
          </tr>
        </thead>

        <tbody>
          <tr th:each="note : ${notes}" th:classappend="${note.done} ? 'done-row' : ''">
            <td class="hide-mobile" th:text="${note.id}" style="color: #94a3b8;">1</td>

            <td>
              <span class="content-text" th:text="${note.text}">記得買牛奶</span>
            </td>

            <td>
              <span
                class="badge-user"
                th:text="${note.authorName != null and !#strings.isEmpty(note.authorName) ? note.authorName : '-'}"
              >
                爸爸
              </span>
            </td>

            <td
              style="font-size: 12px; color: #64748b;"
              th:text="${#temporals.format(note.createdAt, 'yyyy/MM/dd HH:mm')}"
            >
              2025/12/17 10:00
            </td>

            <td>
              <form th:action="@{/notes/{id}/toggle(id=${note.id})}" method="post" style="margin: 0;">
                <button
                  type="submit"
                  class="btn btn-sm"
                  th:style="${note.done} ? 'background:#e2e8f0; color:#64748b;' : 'background:#dcfce7; color:#166534; border:1px solid #bbf7d0;'"
                  th:text="${note.done} ? '復原' : '標示完成'"
                >
                  完成
                </button>
              </form>
            </td>
          </tr>
        </tbody>
      </table>

      <div th:if="${#lists.isEmpty(notes)}" style="text-align: center; padding: 40px; color: var(--muted);">
        目前沒有任何記事，去新增一條吧！
      </div>
    </div>
  </div>
</template>

<script setup>
</script>
