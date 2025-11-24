document.addEventListener('DOMContentLoaded', function() {
    const tabs = document.querySelectorAll('.tab');
    const tabContents = document.querySelectorAll('.tab-content');
    const switches = document.querySelectorAll('.switch input');
    const sliders = document.querySelectorAll('.slider-input');
    const bluetoothBtn = document.querySelector('.bluetooth-btn');
    const statusAlert = document.querySelector('.status-alert');

    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            tabs.forEach(t => t.classList.remove('active'));
            tabContents.forEach(tc => tc.classList.remove('active'));
            this.classList.add('active');
            document.getElementById(this.dataset.tab).classList.add('active');
        });
    });

    switches.forEach(switchEl => {
        switchEl.addEventListener('change', function() {
            // Animação suave já no CSS
        });
    });

    sliders.forEach(slider => {
        slider.addEventListener('input', function() {
            const valueEl = this.previousElementSibling.querySelector('.value');
            valueEl.textContent = this.value + (this.classList.contains('uv') ? '' : ' min');
        });
    });

    bluetoothBtn.addEventListener('click', function() {
        if (statusAlert.classList.contains('connected')) {
            statusAlert.classList.remove('connected');
            statusAlert.classList.add('disconnected');
            statusAlert.innerHTML = '<span class="status-icon">📶</span><span>Desconectada</span>';
            this.textContent = '📶 Conectar';
        } else {
            statusAlert.classList.remove('disconnected');
            statusAlert.classList.add('connected');
            statusAlert.innerHTML = '<span class="status-icon">📶</span><span>Conectada</span>';
            this.textContent = '📶 Desconectar';
        }
    });
});