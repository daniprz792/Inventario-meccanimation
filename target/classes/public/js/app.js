// conecta el html con la api de java 


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

// funcion para editar un item del inventario, llena el formulario con los datos del item seleccionado
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

// evento para enviar el formulario de agregar o editar un item del inventario, si el id del item es nulo se agrega un nuevo item, si no se edita el item existente
document.getElementById('form-item').addEventListener('submit', async (e) => {
    e.preventDefault();

    // obtiene el id del item del formulario, si es nulo se agrega un nuevo item, si no se edita el item existente
    const id = document.getElementById('itemId').value;
// crea un objeto con los datos del item del formulario
    const item = {
        codigoInventario: document.getElementById('codigoInventario').value,
        producto: document.getElementById('producto').value,
        nombre: document.getElementById('nombre').value,
        descripcion: document.getElementById('descripcion').value,
        precioUnitario: parseFloat(document.getElementById('precioUnitario').value),
        cantidadExistencias: parseInt(document.getElementById('cantidadExistencias').value),
        fechaIngreso: document.getElementById('fechaIngreso').value,
        observaciones: document.getElementById('observaciones').value,
        descontinuado: false
    };

    // si el id del item es nulo se agrega un nuevo item, si no se edita el item existente
    if (id) {
        await fetch(`/api/items/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(item)
        });
    } else {
        await fetch('/api/items', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(item)
        });
    }

        // limpia el formulario y recarga la tabla de items
    document.getElementById('form-item').reset();
    document.getElementById('itemId').value = '';
    cargarItems();
});

cargarItems();