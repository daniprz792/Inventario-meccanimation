// conecta el html con la api de java

const modalForm = document.getElementById('modal-form');
const formTitulo = document.getElementById('form-titulo');
const btnAbrirModal = document.getElementById('btn-abrir-modal');

// abre el modal en modo "agregar", limpiando el formulario
btnAbrirModal.addEventListener('click', () => {
    document.getElementById('form-item').reset();
    document.getElementById('itemId').value = '';
    formTitulo.textContent = 'Agregar Producto';
    modalForm.classList.remove('oculto');
});

// cierra el modal del formulario
function cerrarFormulario() {
    modalForm.classList.add('oculto');
}

// funcion para cargar los items del inventario en la tabla
async function cargarItems() {
    const respuesta = await fetch('/api/items');
    const items = await respuesta.json();

    const tbody = document.getElementById('tabla-items');
    tbody.innerHTML = '';
    // recorre cada item y crea una fila en la tabla con los datos del item
    items.forEach(item => {
        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td>${item.codigoInventario}</td>
            <td>${item.producto}</td>
            <td>${item.nombre}</td>
            <td>$${item.precioUnitario.toLocaleString()}</td>
            <td>${item.cantidadExistencias}</td>
            <td>$${item.valorInventario.toLocaleString()}</td>
            <td>
                <button class="btn-detalle" onclick='verDetalle(${JSON.stringify(item)})'>Detalle</button>
                <button class="btn-editar" onclick="editarItem(${item.id}, '${item.codigoInventario}', '${item.producto}', '${item.nombre}', '${item.descripcion ?? ''}', ${item.precioUnitario}, ${item.cantidadExistencias}, '${item.fechaIngreso ?? ''}', '${item.observaciones ?? ''}')">Editar</button>
                <button class="btn-eliminar" onclick="eliminarItem(${item.id})">Eliminar</button>
            </td>
        `;
        tbody.appendChild(fila);
    });
}

// funcion para editar un item: llena el formulario y abre el modal en modo "editar"
function editarItem(id, codigo, producto, nombre, descripcion, precio, cantidad, fecha, observaciones) {
    document.getElementById('itemId').value = id;
    document.getElementById('codigoInventario').value = codigo;
    document.getElementById('producto').value = producto;
    document.getElementById('nombre').value = nombre;
    document.getElementById('descripcion').value = descripcion;
    document.getElementById('precioUnitario').value = precio;
    document.getElementById('cantidadExistencias').value = cantidad;
    document.getElementById('fechaIngreso').value = fecha;
    document.getElementById('observaciones').value = observaciones;

    formTitulo.textContent = 'Editar Producto';
    modalForm.classList.remove('oculto');
}

// funcion para eliminar un item del inventario, pide confirmacion en el navegador antes de eliminar
async function eliminarItem(id) {
    if (!confirm('¿Seguro que quieres eliminar este producto?')) return;
    await fetch(`/api/items/${id}`, { method: 'DELETE' });
    cargarItems();
}

// funcion para ver el detalle de un item del inventario, muestra un modal con los datos del item seleccionado
function verDetalle(item) {
    document.getElementById('detalle-nombre').textContent = item.nombre;
    document.getElementById('detalle-descripcion').textContent = item.descripcion || 'Sin descripción';
    document.getElementById('detalle-observaciones').textContent = item.observaciones || 'Sin observaciones';
    document.getElementById('detalle-fecha').textContent = item.fechaIngreso || 'No registrada';

    const img = document.getElementById('detalle-imagen');
    if (item.imagenProducto) {
        img.src = '/images/' + item.imagenProducto;
        img.style.display = 'block';
    } else {
        img.style.display = 'none';
    }

    document.getElementById('modal-detalle').classList.remove('oculto');
}

// funcion para cerrar el modal de detalle
function cerrarDetalle() {
    document.getElementById('modal-detalle').classList.add('oculto');
}

// evento para enviar el formulario de agregar o editar un item del inventario
// ahora se usa FormData en vez de JSON, para poder incluir el archivo de imagen
document.getElementById('form-item').addEventListener('submit', async (e) => {
    e.preventDefault();

    const id = document.getElementById('itemId').value;

    const datos = new FormData();
    datos.append('codigoInventario', document.getElementById('codigoInventario').value);
    datos.append('producto', document.getElementById('producto').value);
    datos.append('nombre', document.getElementById('nombre').value);
    datos.append('descripcion', document.getElementById('descripcion').value);
    datos.append('precioUnitario', document.getElementById('precioUnitario').value);
    datos.append('cantidadExistencias', document.getElementById('cantidadExistencias').value);
    datos.append('fechaIngreso', document.getElementById('fechaIngreso').value);
    datos.append('observaciones', document.getElementById('observaciones').value);
    datos.append('descontinuado', 'false');

    // solo se agrega el archivo si el usuario seleccionó uno
    const archivoImagen = document.getElementById('imagenProducto').files[0];
    if (archivoImagen) {
        datos.append('imagen', archivoImagen);
    }

    // IMPORTANTE: no poner headers Content-Type aquí; el navegador
    // arma el boundary de multipart automáticamente
    if (id) {
        await fetch(`/api/items/${id}`, {
            method: 'PUT',
            body: datos
        });
    } else {
        await fetch('/api/items', {
            method: 'POST',
            body: datos
        });
    }

    // limpia el formulario, cierra el modal y recarga la tabla
    document.getElementById('form-item').reset();
    document.getElementById('itemId').value = '';
    cerrarFormulario();
    cargarItems();
});

cargarItems();