document.getElementById('form-login').addEventListener('submit', async (e) => {
    e.preventDefault();

    const datos = new FormData();
    datos.append('username', document.getElementById('username').value);
    datos.append('password', document.getElementById('password').value);

    const respuesta = await fetch('/api/login', { method: 'POST', body: datos });

    if (respuesta.ok) {
        window.location.href = 'index.html';
    } else {
        document.getElementById('login-error').textContent = 'Usuario o contraseña incorrectos';
    }
});