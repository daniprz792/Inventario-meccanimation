// conecta el html con la api de java

const modalForm = document.getElementById('modal-form');
const formTitulo = document.getElementById('form-titulo');
const btnAbrirModal = document.getElementById('btn-abrir-modal');
const selectCategoria = document.getElementById('idCategoria');

// carga las categorias en el <select>, con una opcion al final para crear una nueva
async function cargarCategorias(idSeleccionado) {
    const respuesta = await fetch('/api/categorias');
    const categorias = await respuesta.json();

    selectCategoria.innerHTML = '';
    categorias.forEach(cat => {
        const opcion = document.createElement('option');
        opcion.value = cat.id;
        opcion.textContent = cat.nombre;
        selectCategoria.appendChild(opcion);
    });

    const opcionNueva = document.createElement('option');
    opcionNueva.value = '__nueva__';
    opcionNueva.textContent = '+ Nueva categoría...';
    selectCategoria.appendChild(opcionNueva);

    if (idSeleccionado) {
        selectCategoria.value = idSeleccionado;
    }
}

// si el usuario elige "+ Nueva categoría...", pide el nombre y la crea
selectCategoria.addEventListener('change', async () => {
    if (selectCategoria.value !== '__nueva__') return;

    const nombre = prompt('Nombre de la nueva categoría:');
    if (!nombre || !nombre.trim()) {
        await cargarCategorias();
        return;
    }

    const datos = new FormData();
    datos.append('nombre', nombre.trim());

    const respuesta = await fetch('/api/categorias', { method: 'POST', body: datos });
    if (respuesta.ok) {
        const nuevaCategoria = await respuesta.json();
        await cargarCategorias(nuevaCategoria.id);
    } else {
        alert('No se pudo crear la categoría');
        await cargarCategorias();
    }
});

btnAbrirModal.addEventListener('click', async () => {
    document.getElementById('form-item').reset();
    document.getElementById('itemId').value = '';
    formTitulo.textContent = 'Agregar Producto';
    await cargarCategorias();
    modalForm.classList.remove('oculto');
});

function cerrarFormulario() {
    modalForm.classList.add('oculto');
}

async function cargarItems() {
    const respuesta = await fetch('/api/items');
    const items = await respuesta.json();

    const tbody = document.getElementById('tabla-items');
    tbody.innerHTML = '';
    items.forEach(item => {
        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td>${item.codigoInventario}</td>
            <td>${item.nombreCategoria}</td>
            <td>${item.nombre}</td>
            <td>$${item.precioUnitario.toLocaleString()}</td>
            <td>${item.cantidadExistencias}</td>
            <td>$${item.valorInventario.toLocaleString()}</td>
            <td>
                <button class="btn-detalle" onclick='verDetalle(${JSON.stringify(item)})'>Detalle</button>
                <button class="btn-editar" onclick='editarItem(${JSON.stringify(item)})'>Editar</button>
                <button class="btn-eliminar" onclick="eliminarItem(${item.id})">Eliminar</button>
            </td>
        `;
        tbody.appendChild(fila);
    });
}

// recibe el item completo (en vez de una lista larga de parametros) para simplificar
async function editarItem(item) {
    document.getElementById('itemId').value = item.id;
    document.getElementById('codigoInventario').value = item.codigoInventario;
    document.getElementById('nombre').value = item.nombre;
    document.getElementById('marca').value = item.marca ?? '';
    document.getElementById('modelo').value = item.modelo ?? '';
    document.getElementById('descripcion').value = item.descripcion ?? '';
    document.getElementById('precioUnitario').value = item.precioUnitario;
    document.getElementById('cantidadExistencias').value = item.cantidadExistencias;
    document.getElementById('fechaCompra').value = item.fechaCompra ?? '';
    document.getElementById('notas').value = item.notas ?? '';

    await cargarCategorias(item.idCategoria);

    formTitulo.textContent = 'Editar Producto';
    modalForm.classList.remove('oculto');
}

async function eliminarItem(id) {
    if (!confirm('¿Seguro que quieres eliminar este producto?')) return;
    await fetch(`/api/items/${id}`, { method: 'DELETE' });
    cargarItems();
}

function verDetalle(item) {
    document.getElementById('detalle-nombre').textContent = item.nombre;
    document.getElementById('detalle-descripcion').textContent = item.descripcion || 'Sin descripción';
    document.getElementById('detalle-observaciones').textContent = item.notas || 'Sin notas';
    document.getElementById('detalle-fecha').textContent = item.fechaCompra || 'No registrada';

    const img = document.getElementById('detalle-imagen');
    if (item.foto) {
        img.src = '/images/' + item.foto;
        img.style.display = 'block';
    } else {
        img.style.display = 'none';
    }

    document.getElementById('modal-detalle').classList.remove('oculto');
}

function cerrarDetalle() {
    document.getElementById('modal-detalle').classList.add('oculto');
}

document.getElementById('form-item').addEventListener('submit', async (e) => {
    e.preventDefault();

    const id = document.getElementById('itemId').value;

    const datos = new FormData();
    datos.append('codigoInventario', document.getElementById('codigoInventario').value);
    datos.append('idCategoria', document.getElementById('idCategoria').value);
    datos.append('nombre', document.getElementById('nombre').value);
    datos.append('marca', document.getElementById('marca').value);
    datos.append('modelo', document.getElementById('modelo').value);
    datos.append('descripcion', document.getElementById('descripcion').value);
    datos.append('precioUnitario', document.getElementById('precioUnitario').value);
    datos.append('cantidadExistencias', document.getElementById('cantidadExistencias').value);
    datos.append('fechaCompra', document.getElementById('fechaCompra').value);
    datos.append('notas', document.getElementById('notas').value);

    const archivoImagen = document.getElementById('imagenProducto').files[0];
    if (archivoImagen) {
        datos.append('imagen', archivoImagen);
    }

    if (id) {
        await fetch(`/api/items/${id}`, { method: 'PUT', body: datos });
    } else {
        await fetch('/api/items', { method: 'POST', body: datos });
    }

    document.getElementById('form-item').reset();
    document.getElementById('itemId').value = '';
    cerrarFormulario();
    cargarItems();
});

cargarItems();