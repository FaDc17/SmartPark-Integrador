document.addEventListener('DOMContentLoaded', () => {
    const methodButtons = document.querySelectorAll('.method-option');
    const methodPanels = document.querySelectorAll('.method-panel-content');
    const paymentButtons = document.querySelectorAll('.process-payment');
    const receiptCard = document.getElementById('receiptCard');
    const printButton = document.getElementById('printReceipt');
    const modal = document.getElementById('editModal');
    const form = document.getElementById('modalForm');
    const modalVehicleId = document.getElementById('modalVehicleId');
    const modalCliente = document.getElementById('modalCliente');
    const modalPlaca = document.getElementById('modalPlaca');
    const montoInput = document.getElementById('montoRecibido');
    const efectivoResult = document.getElementById('efectivoResult');

    const selectedVehicle = {
        id: document.querySelector('[data-vehicle-id]')?.dataset.vehicleId || '',
        cliente: document.querySelector('[data-vehicle-id]')?.dataset.cliente || '',
        placa: document.querySelector('[data-vehicle-id]')?.dataset.placa || ''
    };

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

    paymentButtons.forEach((button) => {
        button.addEventListener('click', () => {
            const method = button.dataset.paymentMethod;
            const customer = document.getElementById('receiptCliente');
            const placa = document.getElementById('receiptPlaca');
            const total = document.getElementById('receiptTotal');
            const totalLarge = document.getElementById('receiptTotalLarge');
            const methodLabel = document.getElementById('receiptMethod');
            const receiptDate = document.getElementById('receiptFecha');
            const receiptTime = document.getElementById('receiptHora');
            const codeBox = document.getElementById('receiptCode');
            const tiempoBox = document.getElementById('receiptTiempo');
            const now = new Date();
            const code = `SP-2026-${String(Date.now()).slice(-6)}`;

            const summaryStrong = document.querySelectorAll('.summary-grid strong');
            const customerName = summaryStrong[0]?.textContent || 'Carlos Mendoza';
            const placaName = summaryStrong[1]?.textContent || 'ABC-123';
            const totalText = document.querySelector('.total-amount')?.textContent || 'S/ 8.50';
            const tiempoText = summaryStrong[3]?.textContent || '1 h 25 min';
            const methodText = method === 'efectivo' ? 'Efectivo' : method === 'yape' ? 'Yape' : 'Tarjeta de crédito/débito';

            if (customer) customer.textContent = customerName;
            if (placa) placa.textContent = placaName;
            if (total) total.textContent = totalText;
            if (totalLarge) totalLarge.textContent = totalText;
            if (methodLabel) methodLabel.textContent = methodText;
            if (receiptDate) receiptDate.textContent = now.toLocaleDateString('es-PE');
            if (receiptTime) receiptTime.textContent = now.toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' });
            if (codeBox) codeBox.textContent = code;
            if (tiempoBox) tiempoBox.textContent = tiempoText;

            if (receiptCard) {
                receiptCard.classList.remove('hidden');
                receiptCard.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
            }
        });
    });

    if (montoInput) {
        montoInput.addEventListener('input', () => {
            const total = Number(document.querySelector('.total-amount')?.textContent?.replace(/[^0-9.]/g, '') || 8.5);
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
});
