/**
 * CustomDropdown — converte qualquer <select data-drp> no dropdown estilizado.
 * Uso: após popular as options do select, chame dropdown.refresh()
 */
class CustomDropdown {
    constructor(selectEl) {
        this.sel   = selectEl;
        this._build();
    }

    _build() {
        const sel = this.sel;

        this.wrapper  = document.createElement('div');
        this.wrapper.className = 'drp-wrapper';

        this.trigger  = document.createElement('button');
        this.trigger.type = 'button';
        this.trigger.className = 'drp-trigger';

        this.lista = document.createElement('div');
        this.lista.className = 'drp-lista';

        this.trigger.addEventListener('click', e => {
            e.stopPropagation();
            this._toggle();
        });

        this.wrapper.appendChild(this.trigger);
        this.wrapper.appendChild(this.lista);

        // Insere o wrapper antes do select e esconde o select nativo
        sel.parentNode.insertBefore(this.wrapper, sel);
        this.wrapper.appendChild(sel);
        sel.style.display = 'none';

        // Guarda referência no elemento
        sel._dropdown = this;

        this.refresh();
    }

    refresh() {
        const sel = this.sel;
        this.lista.innerHTML = '';

        const placeholder = Array.from(sel.options).find(o => !o.value);
        this.trigger.textContent = placeholder ? placeholder.text : 'Selecione...';
        this.trigger.classList.remove('aberto');
        this.lista.classList.remove('aberta');

        Array.from(sel.options).forEach(opt => {
            if (!opt.value) return;
            const item = document.createElement('div');
            item.className = 'drp-item';
            item.textContent = opt.text;
            item.dataset.valor = opt.value;
            item.addEventListener('click', () => this._selecionar(opt.value, opt.text));
            this.lista.appendChild(item);
        });

        // Mantém valor atual se houver
        if (sel.value) {
            const opt = sel.options[sel.selectedIndex];
            if (opt) this.trigger.textContent = opt.text;
        }
    }

    _selecionar(valor, label) {
        this.sel.value = valor;
        this.trigger.textContent = label;
        this._close();
        this.lista.querySelectorAll('.drp-item').forEach(el =>
            el.classList.toggle('selecionado', el.dataset.valor === valor)
        );
        this.sel.dispatchEvent(new Event('change', { bubbles: true }));
    }

    setValue(valor) {
        const opt = Array.from(this.sel.options).find(o => o.value === valor);
        if (opt) this._selecionar(opt.value, opt.text);
    }

    reset() {
        this.sel.value = '';
        const placeholder = Array.from(this.sel.options).find(o => !o.value);
        this.trigger.textContent = placeholder ? placeholder.text : 'Selecione...';
        this.lista.querySelectorAll('.drp-item').forEach(el => el.classList.remove('selecionado'));
    }

    getValue() { return this.sel.value; }

    _toggle() {
        this.lista.classList.contains('aberta') ? this._close() : this._open();
    }

    _open() {
        // Fecha todos os outros dropdowns abertos
        document.querySelectorAll('.drp-lista.aberta').forEach(l => {
            l.classList.remove('aberta');
            l.previousElementSibling.classList.remove('aberto');
        });
        this.lista.classList.add('aberta');
        this.trigger.classList.add('aberto');
    }

    _close() {
        this.lista.classList.remove('aberta');
        this.trigger.classList.remove('aberto');
    }

    /** Converte todos os <select data-drp> num container (padrão: document) */
    static initAll(container) {
        const root = container || document;
        root.querySelectorAll('select[data-drp]').forEach(sel => {
            if (!sel._dropdown) new CustomDropdown(sel);
        });
    }
}

// Fecha ao clicar fora
document.addEventListener('click', () => {
    document.querySelectorAll('.drp-lista.aberta').forEach(l => {
        l.classList.remove('aberta');
        l.previousElementSibling && l.previousElementSibling.classList.remove('aberto');
    });
});
