<template>
   <main>
    <section class="hero">
      <div class="hero-title"><span>歡迎回來，開始整理你的家庭物資吧。</span></div>
      <p class="hero-sub">
        登入後首頁提供新增、查詢、即將過期與數量不足的快速入口；左上角三條線可依地點或物品類別快速跳轉。
      </p>

      <div class="action-grid">
        <a href="#section-create" class="action-card">
          <div>
            <div class="action-card-title">新增物品</div>
            <div class="action-card-desc">輸入名稱、房間、地點、數量與有效期限，快速建立庫存。</div>
          </div>
          <span class="pill">前往新增區塊</span>
        </a>
        <a href="#section-create" class="action-card">
          <div>
            <div class="action-card-title">查詢物品</div>
            <div class="action-card-desc">可依名稱、SKU、房間、地點或類別查詢目前的存量。</div>
          </div>
          <span class="pill">支援房間查詢</span>
        </a>
        <a href="#section-expire" class="action-card">
          <div>
            <div class="action-card-title">過期查詢</div>
            <div class="action-card-desc">查看已過期與 7 / 14 天內即將到期的物品。</div>
          </div>
          <span class="pill">到期提醒</span>
        </a>
        <a href="#section-low" class="action-card">
          <div>
            <div class="action-card-title">數量不足</div>
            <div class="action-card-desc">檢視庫存過少的物品，避免臨時缺貨。</div>
          </div>
          <span class="pill">低庫存提醒</span>
        </a>
      </div>
    </section>

    <section class="middle-grid">
      <div class="panel">
        <div class="panel-header">
          <div>
            <div class="panel-title">留言 / 交代事項</div>
            <div class="panel-sub">首頁僅顯示未完成事項，完成可勾選。總攬頁面會顯示全部（含已完成）。</div>
          </div>
          <a class="btn btn-ghost btn-sm" th:href="@{/notes}">查看全部事項總覽</a>
        </div>

        <form th:action="@{/notes}" method="post" class="note-input-row">
          <input name="text" placeholder="輸入要交代的事情，例如：週五前確認冰箱雞蛋數量" required />
          <button type="submit" class="btn btn-primary">新增</button>
        </form>

        <ul class="note-list">
          <li class="note-item" th:each="note : ${openNotes}">
            <form class="inline" th:action="@{|/notes/${note.id}/toggle|}" method="post">
              <input type="checkbox" onchange="this.form.submit()" />
            </form>

            <div class="note-text" th:text="${note.text}">把客廳零食區重新整理</div>

            <!-- ✅ createdAt null-safe -->
            <div class="note-time" th:if="${note.createdAt != null}"
                 th:text="${#temporals.format(note.createdAt,'MM/dd HH:mm')}">11/17 21:05</div>
            <div class="note-time" th:if="${note.createdAt == null}">--</div>
          </li>
        </ul>
      </div>

      <div class="panel">
        <div class="panel-header">
          <div class="panel-title">家庭行事曆</div>
        </div>
        <div class="calendar-header">
          <button type="button" class="btn btn-ghost btn-sm" id="calPrev">&lt;</button>
          <div class="calendar-title" id="calTitle">2025 / 11</div>
          <button type="button" class="btn btn-ghost btn-sm" id="calNext">&gt;</button>
        </div>
        <div class="calendar-grid" id="calendarGrid"></div>
      </div>
    </section>

    <section class="section" id="section-create">
      <div class="section-header">
        <div>
          <div class="section-title">新增 / 查詢物品</div>
          <div class="section-sub">查詢時以「名稱」為必填，其餘選填；新增時請填寫儲位與數量，過期日期可選填。</div>
        </div>
      </div>

      <form th:object="${item}" id="createSearchForm" method="post" th:action="@{/items}">
        <div class="row">
          <div class="field">
            <label>名稱（查詢時必填）</label>
            <div class="ai-input-wrap">
              <input th:field="*{name}" id="nameField" placeholder="螺絲 M3 / 牛奶 / 洗衣精"/>
              <button type="button" class="btn btn-ghost btn-sm btn-ai" id="btnAiSuggest">自動填寫</button>
            </div>
          </div>

          <div class="field">
            <label>SKU</label>
            <input th:field="*{sku}" id="skuField" placeholder="唯一代碼（選填）"/>
          </div>

          <div class="field">
  <label>物品類別</label>

  <select id="categorySelect-create">
    <option value="" selected>選擇類別（選填）</option>
    <option th:each="cat : ${categories}" th:value="${cat}" th:text="${cat}">食材</option>
    <option value="__NEW__">＋新增類別…</option>
  </select>

  <input id="categoryInput-create" type="text" placeholder="輸入新類別" style="display:none;" />

  <input type="hidden" th:field="*{category}" id="categoryField-create"/>
</div>


          

          <div class="field">
            <label>單位</label>
            <input th:field="*{unit}" placeholder="pcs/box/kg（選填）"/>
          </div>

          <div class="field" style="max-width:220px">
            <label>數量（新增用）</label>
            <input type="number" th:field="*{quantity}" min="0" value="0"/>
          </div>

          <div class="field">
            <label>儲位</label>
            <select id="locationSelect-create">
              <option value="" selected>選擇地點（選填或新增時必填）</option>
              <option th:each="loc : ${locations}" th:value="${loc}" th:text="${loc}">A1-3</option>
              <option value="__NEW__">＋新增地點…</option>
            </select>
            <input id="locationInput-create" type="text" placeholder="輸入新地點" style="display:none;"/>
            <input type="hidden" th:field="*{location}" id="locationField-create"/>
          </div>

          <div class="field">
            <label>有效期限（選填）</label>
            <input type="date" th:field="*{expireDate}"/>
          </div>

          <div class="field--actions">
            <button type="submit" class="btn btn-primary" id="btnCreate">新增</button>
            <button class="btn btn-ghost" id="btnSearch" formaction="/items/search" formmethod="get">查詢</button>
          </div>
        </div>

        <div class="form-help">
          新增時請選擇或輸入儲位；查詢時僅「名稱」必填，其餘欄位可協助縮小範圍。
        </div>
      </form>
    </section>

    <section class="section" id="section-list">
      <div class="section-header">
        <div>
          <div class="section-title">物料清單</div>
          <div class="section-sub">此清單顯示目前條件下的物品；每一列支援 +1 / -1、刪除與大量修改數量。</div>
        </div>
      </div>

      <table>
        <thead>
        <tr>
          <th>ID</th>
          <th>名稱 / 類別</th>
          <th>SKU</th>
          <th>儲位</th>
          <th>單位 / 數量</th>
          <th>有效期限</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        
        <tr th:each="it : ${items}"
            th:classappend="${it.quantity != null and it.quantity <= 0} ? 'low-stock-row' : ''">
          <td th:text="${it.id}">1</td>

          <td>
            <div th:text="${it.name}">螺絲</div>
            <div style="font-size:11px; color:var(--muted);"
                 th:text="${it.category != null and !#strings.isEmpty(it.category) ? it.category : '-'}">五金</div>
          </td>

          <td th:text="${it.sku != null and !#strings.isEmpty(it.sku) ? it.sku : '-'}">SCREW-M3</td>

          <td>
           
            <div>儲位：<strong th:text="${it.location != null and !#strings.isEmpty(it.location) ? it.location : 'UNASSIGNED'}">A1-3</strong></div>
          </td>

          <td>
            <div>
              <span th:text="${it.unit != null and !#strings.isEmpty(it.unit) ? it.unit : '-'}">pcs</span>
              ×
              <span th:text="${it.quantity != null ? it.quantity : 0}">100</span>
            </div>
            <div th:if="${it.quantity != null and it.quantity <= 0}" class="badge badge-low">數量不足</div>
          </td>

          <!-- ✅ 不呼叫 it.daysToExpire，直接用 ChronoUnit 在模板算 -->
          <td th:with="d=${it.expireDate != null ? T(java.time.temporal.ChronoUnit).DAYS.between(T(java.time.LocalDate).now(), it.expireDate) : null}">
            <div th:if="${it.expireDate != null}"
                 th:text="${#temporals.format(it.expireDate,'yyyy/MM/dd')}">2025/12/31</div>
            <div th:if="${it.expireDate == null}" style="color: var(--muted);">-</div>

            <div th:if="${d != null}">
              <span class="badge badge-exp7"  th:if="${d <= 7 and d >= 0}">7 天內到期</span>
              <span class="badge badge-exp14" th:if="${d <= 14 and d > 7}">14 天內到期</span>
            </div>
          </td>

          <td>
            <form class="inline" th:action="@{|/items/${it.id}/adjust|}" method="post">
              <input type="hidden" name="delta" value="1">
              <button type="submit" class="btn btn-ghost btn-sm">+1</button>
            </form>
            <form class="inline" th:action="@{|/items/${it.id}/adjust|}" method="post">
              <input type="hidden" name="delta" value="-1">
              <button type="submit" class="btn btn-ghost btn-sm">-1</button>
            </form>

            <button type="button"
                    class="btn btn-ghost btn-sm btn-editQty"
                    th:attr="data-id=${it.id},data-current=${it.quantity != null ? it.quantity : 0}">
              更改數量
            </button>

            <form class="inline" th:action="@{|/items/${it.id}/delete|}" method="post"
                  onsubmit="return confirm('確定刪除？')">
              <button type="submit" class="btn btn-ghost btn-sm">刪除</button>
            </form>
          </td>
        </tr>
        </tbody>
      </table>
    </section>

    <section class="section" id="section-expire">
      <div class="section-header">
        <div>
          <div class="section-title">過期 / 即將到期物品</div>
          <div class="section-sub">分成「已過期」、「7 天內到期」、「14 天內到期」，方便你快速處理。</div>
        </div>
      </div>

      <div style="font-weight:700; color:#123763; margin:6px 0 6px;">已過期</div>
      <table th:if="${expiredItems != null and #lists.size(expiredItems) > 0}">
        <thead>
        <tr>
          <th>ID</th><th>名稱</th><th>類別</th><th>房間/儲位</th><th>數量</th><th>到期日</th>
        </tr>
        </thead>
        <tbody>
        <tr th:each="it : ${expiredItems}">
          <td th:text="${it.id}">1</td>
          <td th:text="${it.name}">牛奶</td>
          <td th:text="${it.category != null and !#strings.isEmpty(it.category) ? it.category : '-'}">食材</td>
          <td>
            房間：<span th:text="${it.room != null and !#strings.isEmpty(it.room) ? it.room : '-'}"></span>
            ／ 儲位：<strong th:text="${it.location != null and !#strings.isEmpty(it.location) ? it.location : 'UNASSIGNED'}"></strong>
          </td>
          <td>
            <span th:text="${it.unit != null and !#strings.isEmpty(it.unit) ? it.unit : '-'}"></span>
            ×
            <span th:text="${it.quantity != null ? it.quantity : 0}"></span>
          </td>
          <td>
            <div th:if="${it.expireDate != null}" th:text="${#temporals.format(it.expireDate,'yyyy/MM/dd')}"></div>
            <div th:if="${it.expireDate == null}" style="color:var(--muted);">-</div>
            <span class="badge badge-low">已過期</span>
          </td>
        </tr>
        </tbody>
      </table>
      <p th:if="${expiredItems == null or #lists.size(expiredItems) == 0}"
         style="font-size:13px;color:var(--muted);margin:0 0 12px;">
        目前沒有已過期物品 ✅
      </p>

      <div style="font-weight:700; color:#123763; margin:10px 0 6px;">7 天內到期</div>
      <table th:if="${exp7Items != null and #lists.size(exp7Items) > 0}">
        <thead>
        <tr>
          <th>ID</th><th>名稱</th><th>房間/儲位</th><th>數量</th><th>到期日</th><th>剩餘</th>
        </tr>
        </thead>
        <tbody>
        <tr th:each="it : ${exp7Items}"
            th:with="d=${it.expireDate != null ? T(java.time.temporal.ChronoUnit).DAYS.between(T(java.time.LocalDate).now(), it.expireDate) : null}">
          <td th:text="${it.id}">1</td>
          <td>
            <div th:text="${it.name}"></div>
            <div style="font-size:11px; color:var(--muted);" th:text="${it.category != null and !#strings.isEmpty(it.category) ? it.category : '-'}"></div>
          </td>
          <td>
            房間：<span th:text="${it.room != null and !#strings.isEmpty(it.room) ? it.room : '-'}"></span>
            ／ 儲位：<strong th:text="${it.location != null and !#strings.isEmpty(it.location) ? it.location : 'UNASSIGNED'}"></strong>
          </td>
          <td>
            <span th:text="${it.unit != null and !#strings.isEmpty(it.unit) ? it.unit : '-'}"></span>
            ×
            <span th:text="${it.quantity != null ? it.quantity : 0}"></span>
          </td>
          <td>
            <div th:if="${it.expireDate != null}" th:text="${#temporals.format(it.expireDate,'yyyy/MM/dd')}"></div>
            <div th:if="${it.expireDate == null}" style="color:var(--muted);">-</div>
          </td>
          <td>
            <span class="badge badge-exp7" th:if="${d != null}" th:text="${d} + ' 天'">3 天</span>
            <span th:if="${d == null}" style="color:var(--muted);">-</span>
          </td>
        </tr>
        </tbody>
      </table>
      <p th:if="${exp7Items == null or #lists.size(exp7Items) == 0}"
         style="font-size:13px;color:var(--muted);margin:0 0 12px;">
        目前沒有 7 天內到期物品 ✅
      </p>

      <div style="font-weight:700; color:#123763; margin:10px 0 6px;">14 天內到期</div>
      <table th:if="${exp14Items != null and #lists.size(exp14Items) > 0}">
        <thead>
        <tr>
          <th>ID</th><th>名稱</th><th>房間/儲位</th><th>數量</th><th>到期日</th><th>剩餘</th>
        </tr>
        </thead>
        <tbody>
        <tr th:each="it : ${exp14Items}"
            th:with="d=${it.expireDate != null ? T(java.time.temporal.ChronoUnit).DAYS.between(T(java.time.LocalDate).now(), it.expireDate) : null}">
          <td th:text="${it.id}">1</td>
          <td>
            <div th:text="${it.name}"></div>
            <div style="font-size:11px; color:var(--muted);" th:text="${it.category != null and !#strings.isEmpty(it.category) ? it.category : '-'}"></div>
          </td>
          <td>
            房間：<span th:text="${it.room != null and !#strings.isEmpty(it.room) ? it.room : '-'}"></span>
            ／ 儲位：<strong th:text="${it.location != null and !#strings.isEmpty(it.location) ? it.location : 'UNASSIGNED'}"></strong>
          </td>
          <td>
            <span th:text="${it.unit != null and !#strings.isEmpty(it.unit) ? it.unit : '-'}"></span>
            ×
            <span th:text="${it.quantity != null ? it.quantity : 0}"></span>
          </td>
          <td>
            <div th:if="${it.expireDate != null}" th:text="${#temporals.format(it.expireDate,'yyyy/MM/dd')}"></div>
            <div th:if="${it.expireDate == null}" style="color:var(--muted);">-</div>
          </td>
          <td>
            <span class="badge badge-exp14" th:if="${d != null}" th:text="${d} + ' 天'">10 天</span>
            <span th:if="${d == null}" style="color:var(--muted);">-</span>
          </td>
        </tr>
        </tbody>
      </table>
      <p th:if="${exp14Items == null or #lists.size(exp14Items) == 0}"
         style="font-size:13px;color:var(--muted);margin:0;">
        目前沒有 14 天內到期物品 ✅
      </p>
    </section>
  </main>
</template>

<script setup>
import { onMounted } from 'vue'

onMounted(() => {
  // 🔽 直接貼你 items.html 裡 <script> 的 JS（modal、select、merge 邏輯）
})
</script>
