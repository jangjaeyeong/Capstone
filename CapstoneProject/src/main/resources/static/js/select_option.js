const stackData = {
  frontend: [
    "HTML, CSS, JavaScript",
    "React.js, Vue.js",
    "Figma, Git, Vite",
    "웹 접근성, 반응형 디자인"
  ],
  backend: [
    "Java, Python, Node.js",
    "Spring Boot, Django, Express",
    "MySQL, MongoDB, Redis",
    "REST API, JWT, 서버 보안"
  ],
  fullstack: [
    "HTML, CSS, JS + React or Vue",
    "Node.js + Express / Django",
    "PostgreSQL or MongoDB",
    "CI/CD, AWS 또는 Firebase"
  ],
  ai: [
    "Python, NumPy, Pandas",
    "Scikit-learn, TensorFlow, PyTorch",
    "Jupyter Notebook, Colab",
    "기초 수학, 통계, 딥러닝 이론"
  ],
  mobile: [
    "Java/Kotlin (Android), Swift (iOS)",
    "React Native, Flutter",
    "Firebase, SQLite",
    "모바일 UI/UX, 앱 배포"
  ],
  security: [
    "C, Python, 리버싱 기초",
    "네트워크, 암호학",
    "Kali Linux, Wireshark, Burp Suite",
    "OWASP, 모의 해킹"
  ]
};

function recommendStack() {
  const goal = document.getElementById("goalSelect").value;
  const resultDiv = document.getElementById("result");
  const resultSection = document.getElementById("resultSection");

  if (!goal) {
    alert("목표를 선택해주세요.");
    return;
  }

  const stackList = stackData[goal];
  resultDiv.innerHTML = `<ul>${stackList.map(item => `<li>${item}</li>`).join("")}</ul>`;
  resultSection.style.display = "block";
}