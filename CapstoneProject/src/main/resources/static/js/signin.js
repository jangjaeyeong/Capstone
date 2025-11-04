/* document.addEventListener("DOMContentLoaded", function () {
  const emailInput = document.querySelector('form.login input[type="text"]');
  const rememberCheckbox = document.getElementById("remember-id");

  const savedId = localStorage.getItem("rememberedId");
  if (savedId) {
    emailInput.value = savedId;
    rememberCheckbox.checked = true;
  }

  const loginForm = document.querySelector("form.login");
  loginForm.addEventListener("submit", function () {
    if (rememberCheckbox.checked) {
    localStorage.setItem("rememberedId", emailInput.value);
    } else {
      localStorage.removeItem("rememberedId");
    }
  });

  const loginText = document.querySelector(".title-text .login");
  const loginFormElement = document.querySelector("form.login");
  const loginBtn = document.querySelector("label.login");
  const signupBtn = document.querySelector("label.signup");
  const signupLink = document.querySelector("form .signup-link a");

  signupBtn.onclick = () => {
    loginFormElement.style.marginLeft = "-50%";
    loginText.style.marginLeft = "-50%";
  };
  loginBtn.onclick = () => {
    loginFormElement.style.marginLeft = "0%";
    loginText.style.marginLeft = "0%";
  };
  signupLink.onclick = () => {
    signupBtn.click();
    return false;
  };
}); */

document.addEventListener("DOMContentLoaded", function () {
  const emailInput = document.querySelector('form.login input[type="text"]');
  const rememberCheckbox = document.getElementById("remember-id");

  // 기억된 ID 불러오기
  const savedId = localStorage.getItem("rememberedId");
  if (savedId) {
    emailInput.value = savedId;
    rememberCheckbox.checked = true;
  }



    const email = emailInput.value;

    // ID 기억 처리
    if (rememberCheckbox.checked) {
      localStorage.setItem("rememberedId", email);
    } else {
      localStorage.removeItem("rememberedId");
    }

    // 로그인 상태 저장
    localStorage.setItem("loggedInUser", email);


  // 탭 전환 관련 요소들
  const loginText = document.querySelector(".title-text .login");
  const loginFormElement = document.querySelector("form.login");
  const loginBtn = document.querySelector("label.login");
  const signupBtn = document.querySelector("label.signup");
  const signupLink = document.querySelector("form .signup-link a");

  // SIGN UP 탭 클릭 시
  signupBtn.onclick = () => {
    loginFormElement.style.marginLeft = "-50%";
    loginText.style.marginLeft = "-50%";
  };

  // SIGN IN 탭 클릭 시
  loginBtn.onclick = () => {
    loginFormElement.style.marginLeft = "0%";
    loginText.style.marginLeft = "0%";
  };

  // 회원가입 링크 누르면 자동으로 SIGN UP 탭으로 전환
  signupLink.onclick = () => {
    signupBtn.click();
    return false;
  };
});