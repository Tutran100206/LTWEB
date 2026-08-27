// Shared UI interactions: page transition, ripple, password toggle, login form validation
(function () {
  function qs(selector) { return document.querySelector(selector); }

  function animatePageIn() {
    requestAnimationFrame(function () {
      document.body.classList.add('page-ready');
    });
  }

  function setupPageLeaveTransition() {
    document.addEventListener('click', function (e) {
      var link = e.target.closest('a');
      if (!link) return;
      if (link.target === '_blank' || link.hasAttribute('download')) return;
      if (link.origin !== window.location.origin) return;
      if (link.getAttribute('href') && link.getAttribute('href').indexOf('#') === 0) return;

      e.preventDefault();
      document.body.classList.add('page-leaving');
      setTimeout(function () {
        window.location.href = link.href;
      }, 230);
    });
  }

  function setupRipple() {
    document.addEventListener('click', function (e) {
      var btn = e.target.closest('.ripple');
      if (!btn) return;

      var rect = btn.getBoundingClientRect();
      var span = document.createElement('span');
      var size = Math.max(rect.width, rect.height);
      span.style.width = size + 'px';
      span.style.height = size + 'px';
      span.style.left = (e.clientX - rect.left - (size / 2)) + 'px';
      span.style.top = (e.clientY - rect.top - (size / 2)) + 'px';
      btn.appendChild(span);
      setTimeout(function () { span.remove(); }, 700);
    });
  }

  function setupPasswordToggle() {
    var pwdToggle = qs('#togglePwd');
    if (!pwdToggle) return;

    pwdToggle.addEventListener('click', function () {
      var pwd = qs('#password');
      if (!pwd) return;
      if (pwd.type === 'password') {
        pwd.type = 'text';
        pwdToggle.textContent = 'Ẩn';
      } else {
        pwd.type = 'password';
        pwdToggle.textContent = 'Hiện';
      }
    });
  }

  function setupLoginValidation() {
    var form = qs('form[data-auth-form="true"]');
    if (!form) return;

    form.addEventListener('submit', function (e) {
      var card = qs('.card');
      var user = qs('#username');
      var pass = qs('#password');
      var invalidUser = !user || !user.value.trim();
      var invalidPass = !pass || !pass.value.trim();
      if (!invalidUser && !invalidPass) return;

      e.preventDefault();
      if (card) {
        card.classList.remove('shake');
        void card.offsetWidth;
        card.classList.add('shake');
      }
      if (invalidUser && user) user.focus();
      if (!invalidUser && invalidPass && pass) pass.focus();
    });
  }

  animatePageIn();
  setupPageLeaveTransition();
  setupRipple();
  setupPasswordToggle();
  setupLoginValidation();
})();