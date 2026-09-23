async function cargarItems() {
    const respuesta = await fetch('/api/items');
    const items = await respuesta.json();

    const tbody = document.getElementById('tabla-items');
    tbody.innerHTML = '';

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
                <button class="btn-editar" onclick="editarItem(${item.id}, '${item.codigoInventario}', '${item.producto}', '${item.nombre}', '${item.descripcion ?? ''}', ${item.precioUnitario}, ${item.cantidadExistencias}, '${item.fechaIngreso ?? ''}', '${item.observaciones ?? ''}')">Editar</button>
                <button class="btn-eliminar" onclick="eliminarItem(${item.id})">Eliminar</button>
            </td>
        `;
        tbody.appendChild(fila);
    });
}

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

async function eliminarItem(id) {
    if (!confirm('¿Seguro que quieres eliminar este producto?')) return;
    await fetch(`/api/items/${id}`, { method: 'DELETE' });
    cargarItems();
}

document.getElementById('form-item').addEventListener('submit', async (e) => {
    e.preventDefault();

    const id = document.getElementById('itemId').value;

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

    document.getElementById('form-item').reset();
    document.getElementById('itemId').value = '';
    cargarItems();
});

cargarItems();