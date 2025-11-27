// Cadastro com validação de senha
const registerForm = document.getElementById('registerForm');
registerForm.addEventListener('submit', function(e){
  e.preventDefault();
  
  const password = document.getElementById('password').value;
  const confirmPassword = document.getElementById('confirmPassword').value;

  if(password !== confirmPassword){
    alert("As senhas não coincidem!");
    return;
  }

  // Pegando informações de pele
  const skinColor = document.querySelector('input[name="skinColor"]:checked')?.value;
  const skinCondition = document.querySelector('input[name="skinCondition"]:checked')?.value;
  const pigmentation = document.querySelector('input[name="pigmentation"]:checked')?.value || 'N/A';

  alert(`Cadastro efetuado com sucesso!\nCor da Pele: ${skinColor}\nCondição: ${skinCondition}\nNível de Pigmentação: ${pigmentation}`);
});

// Mostrar nível de pigmentação apenas se Vitiligo
const skinConditionRadios = document.getElementsByName('skinCondition');
const pigmentationDiv = document.getElementById('pigmentationLevel');

skinConditionRadios.forEach(radio => {
  radio.addEventListener('change', () => {
    if (radio.value === 'Vitiligo' && radio.checked) {
      pigmentationDiv.classList.remove('hidden');
    } else {
      pigmentationDiv.classList.add('hidden');
    }
  });
});

// Salvando dados no LocalStorage
let users = JSON.parse(localStorage.getItem('users')) || [];
users.push({ nome: name, email, password });
localStorage.setItem('users', JSON.stringify(users));
