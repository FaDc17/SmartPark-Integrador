document.addEventListener('DOMContentLoaded', () => {
    const methodButtons = document.querySelectorAll('.method-option');
    const methodPanels = document.querySelectorAll('.method-panel-content');
    const printButton = document.getElementById('printReceipt');
    const modal = document.getElementById('editModal');
    const form = document.getElementById('modalForm');
    const modalVehicleId = document.getElementById('modalVehicleId');
    const modalCliente = document.getElementById('modalCliente');
    const modalPlaca = document.getElementById('modalPlaca');
    const montoInput = document.getElementById('montoRecibido');
    const efectivoResult = document.getElementById('efectivoResult');

    if (methodButtons.length) {
        methodButtons.forEach((button) => {
            button.addEventListener('click', () => {
                const method = button.dataset.method;
                methodButtons.forEach((btn) => btn.classList.toggle('active', btn === button));
                methodPanels.forEach((panel) => {
                    panel.classList.toggle('active', panel.dataset.content === method);
                });
            });
        });
    }

    if (montoInput) {
        const leerTotal = () => {
            const texto = document.querySelector('.total-amount')?.textContent || '';
            let limpio = texto.replace(/[^0-9.,]/g, '');
            if (limpio.includes(',')) {
                limpio = limpio.replace(/\./g, '').replace(',', '.');
            }
            const valor = parseFloat(limpio);
            return Number.isNaN(valor) ? 0 : valor;
        };

        montoInput.addEventListener('input', () => {
            const total = leerTotal();
            const received = Number(montoInput.value || 0);
            if (!montoInput.value) {
                efectivoResult.classList.add('hidden');
                return;
            }
            const diff = received - total;
            efectivoResult.classList.remove('hidden');
            if (diff >= 0) {
                efectivoResult.textContent = `Vuelto: S/ ${diff.toFixed(2)}`;
                efectivoResult.classList.remove('pending');
            } else {
                efectivoResult.textContent = `Saldo pendiente: S/ ${Math.abs(diff).toFixed(2)}`;
                efectivoResult.classList.add('pending');
            }
        });
    }

    if (printButton) {
        printButton.addEventListener('click', () => window.print());
    }

    document.querySelectorAll('.open-modal').forEach((button) => {
        button.addEventListener('click', () => {
            if (!modal) return;
            modalVehicleId.value = button.dataset.id || '';
            modalCliente.value = button.dataset.cliente || '';
            modalPlaca.value = button.dataset.placa || '';
            modal.classList.add('visible');
        });
    });

    document.querySelectorAll('.close-modal').forEach((button) => {
        button.addEventListener('click', () => {
            if (modal) modal.classList.remove('visible');
        });
    });

    if (modal) {
        modal.addEventListener('click', (event) => {
            if (event.target === modal) {
                modal.classList.remove('visible');
            }
        });
    }

    if (form) {
        form.addEventListener('submit', (event) => {
            event.preventDefault();
            if (modalVehicleId.value && modalCliente.value.trim() && modalPlaca.value.trim()) {
                form.submit();
            }
        });
    }

    document.querySelectorAll('.toggle-password').forEach((button) => {
        button.addEventListener('click', () => {
            const input = button.parentElement.querySelector('input');
            const show = input.type === 'password';
            input.type = show ? 'text' : 'password';
            button.classList.toggle('visible', show);
            button.setAttribute('aria-label', show ? 'Ocultar contraseña' : 'Mostrar contraseña');
        });
    });
});
