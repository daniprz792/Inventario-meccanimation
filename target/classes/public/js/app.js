// conecta el html con la api de java

const modalForm = document.getElementById('modal-form');
const formTitulo = document.getElementById('form-titulo');
const btnAbrirModal = document.getElementById('btn-abrir-modal');
const selectCategoria = document.getElementById('idCategoria');

//  modal de confirmacion reutilizable (reemplaza confirm()) 

const modalConfirmacion = document.getElementById('modal-confirmacion');
const confirmacionMensaje = document.getElementById('confirmacion-mensaje');
const btnConfirmacionAceptar = document.getElementById('confirmacion-aceptar');
const btnConfirmacionCancelar = document.getElementById('confirmacion-cancelar');

// muestra el modal con el mensaje dado y devuelve una Promise<boolean>:

// true si el usuario acepta, false si cancela o cierra el modal
function mostrarConfirmacion(mensaje) {
    confirmacionMensaje.textContent = mensaje;
    modalConfirmacion.classList.remove('oculto');

    return new Promise(resolve => {
        const limpiar = () => {
            modalConfirmacion.classList.add('oculto');
            btnConfirmacionAceptar.removeEventListener('click', onAceptar);
            btnConfirmacionCancelar.removeEventListener('click', onCancelar);
        };
        const onAceptar = () => { limpiar(); resolve(true); };
        const onCancelar = () => { limpiar(); resolve(false); };

        btnConfirmacionAceptar.addEventListener('click', onAceptar);
        btnConfirmacionCancelar.addEventListener('click', onCancelar);
    });
}

//  categorias 

async function cargarCategorias(idSeleccionado) {
    const respuesta = await fetch('/api/categorias');
    const categorias = await respuesta.json();

    selectCategoria.innerHTML = '';
    if (categorias.length === 0) {
        const opcionVacia = document.createElement('option');
        opcionVacia.value = '';
        opcionVacia.textContent = 'Seleccione una categoría';
        opcionVacia.disabled = true;
        opcionVacia.selected = true;
        selectCategoria.appendChild(opcionVacia);
    }

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

const modalCategoria = document.getElementById('modal-categoria');
const inputNuevaCategoria = document.getElementById('nombreNuevaCategoria');

selectCategoria.addEventListener('change', () => {
    if (selectCategoria.value !== '__nueva__') return;
    inputNuevaCategoria.value = '';
    modalCategoria.classList.remove('oculto');
    inputNuevaCategoria.focus();
});

// si cancela, regresamos el select a la primera categoria real (no dejamos "+ Nueva..." seleccionado)
function cancelarNuevaCategoria() {
    modalCategoria.classList.add('oculto');
    if (selectCategoria.options.length > 1) {
        selectCategoria.selectedIndex = 0;
    }
}

document.getElementById('form-categoria').addEventListener('submit', async (e) => {
    e.preventDefault();

    const nombre = inputNuevaCategoria.value.trim();
    if (!nombre) return;

    const datos = new FormData();
    datos.append('nombre', nombre);

    const respuesta = await fetch('/api/categorias', { method: 'POST', body: datos });
    if (respuesta.ok) {
        const nuevaCategoria = await respuesta.json();
        await cargarCategorias(nuevaCategoria.id);
        modalCategoria.classList.add('oculto');
    } else {
        alert('No se pudo crear la categoría');
        await cargarCategorias();
        modalCategoria.classList.add('oculto');
    }
});

//  modal de agregar/editar producto 

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

//  búsqueda en tiempo real 

const inputBuscar = document.getElementById('buscarProducto');

function filtrarTabla() {
    const termino = inputBuscar.value.trim().toLowerCase();
    const filas = document.querySelectorAll('#tabla-items tr');

    filas.forEach(fila => {
        const codigo = fila.children[0]?.textContent.toLowerCase() || '';
        const categoria = fila.children[1]?.textContent.toLowerCase() || '';
        const nombre = fila.children[2]?.textContent.toLowerCase() || '';

        const coincide = codigo.includes(termino) || categoria.includes(termino) || nombre.includes(termino);
        fila.style.display = coincide ? '' : 'none';
    });
}

inputBuscar.addEventListener('input', filtrarTabla);

//  tabla de productos
async function cargarItems() {
    const respuesta = await fetch('/api/items');
    const items = await respuesta.json();

    const tbody = document.getElementById('tabla-items');
    tbody.innerHTML = '';
    items.forEach(item => {
        const fila = document.createElement('tr');

        /*const botonUnidades = item.requiereUnidades
            ? `<button class="btn-detalle" onclick="abrirUnidades(${item.id}, '${item.nombre}')">Ver unidades</button>`
            : `<span style="opacity:.5">N/A</span>`;*/

        fila.innerHTML = `
            <td>${item.codigoInventario}</td>
            <td>${item.nombreCategoria}</td>
            <td>${item.nombre}</td>
            <td>$${item.precioUnitario.toLocaleString()}</td>
            <td>${item.descripcion || 'Sin descripción'}</td>
            <td>${item.notas || 'Sin observaciones'}</td>
            <td>${item.fechaCompra || 'No registrada'}</td>
            <td>
                <button class="btn-detalle" onclick='verDetalle(${JSON.stringify(item)})'>Ver imagen</button>
                <button class="btn-editar" onclick='editarItem(${JSON.stringify(item)})'>Editar</button>
                <button class="btn-eliminar" onclick="eliminarItem(${item.id})">Eliminar</button>
            </td>
        `;
        tbody.appendChild(fila);
    });

    filtrarTabla(); // reaplica el filtro activo despues de recargar la tabla
}

async function editarItem(item) {
    document.getElementById('itemId').value = item.id;
    document.getElementById('codigoInventario').value = item.codigoInventario;
    document.getElementById('nombre').value = item.nombre;
    document.getElementById('marca').value = item.marca ?? '';
    document.getElementById('modelo').value = item.modelo ?? '';
    document.getElementById('descripcion').value = item.descripcion ?? '';
    document.getElementById('precioUnitario').value = item.precioUnitario;
    document.getElementById('fechaCompra').value = item.fechaCompra ?? '';
    document.getElementById('notas').value = item.notas ?? '';

    await cargarCategorias(item.idCategoria);

    formTitulo.textContent = 'Editar Producto';
    modalForm.classList.remove('oculto');
}

async function eliminarItem(id) {
    const confirmado = await mostrarConfirmacion('¿Seguro que quieres eliminar este producto?');
    if (!confirmado) return;
    const respuesta = await fetch(`/api/items/${id}`, { method: 'DELETE' });
    if (!respuesta.ok) {
        alert('No se pudo eliminar el producto');
        return;
    }
    await cargarItems();
}

function verDetalle(item) {
    const img = document.getElementById('detalle-imagen');
    if (item.foto) {
        img.src = '/images/' + item.foto;
        img.style.display = 'block';
    } else {
        img.removeAttribute('src');
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
    datos.append('fechaCompra', document.getElementById('fechaCompra').value);
    datos.append('notas', document.getElementById('notas').value);

    const archivoImagen = document.getElementById('imagenProducto').files[0];
    if (archivoImagen) {
        datos.append('imagen', archivoImagen);
    }

    let respuesta;
    if (id) {
        respuesta = await fetch(`/api/items/${id}`, { method: 'PUT', body: datos });
    } else {
        respuesta = await fetch('/api/items', { method: 'POST', body: datos });
    }

    if (!respuesta.ok) {
        const mensaje = await respuesta.text();
        alert(mensaje || 'Ocurrió un error al guardar el producto');
        return;
    }

    document.getElementById('form-item').reset();
    document.getElementById('itemId').value = '';
    cerrarFormulario();
    cargarItems();
});

//  modal de unidades individuales 

const modalUnidades = document.getElementById('modal-unidades');
const unidadesTitulo = document.getElementById('unidades-titulo');
const unidadProductoIdInput = document.getElementById('unidadProductoId');

async function abrirUnidades(idProducto, nombreProducto) {
    unidadProductoIdInput.value = idProducto;
    unidadesTitulo.textContent = `Unidades de: ${nombreProducto}`;
    document.getElementById('form-unidad').reset();
    unidadProductoIdInput.value = idProducto; // el reset() borra el hidden, lo volvemos a poner
    await cargarUnidades(idProducto);
    modalUnidades.classList.remove('oculto');
}

function cerrarUnidades() {
    modalUnidades.classList.add('oculto');
    cargarItems(); // por si cambio la cantidad de unidades, refresca la tabla principal
}

async function cargarUnidades(idProducto) {
    const respuesta = await fetch(`/api/productos/${idProducto}/unidades`);
    const unidades = await respuesta.json();

    const tbody = document.getElementById('tabla-unidades');
    tbody.innerHTML = '';

    unidades.forEach(unidad => {
        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td>${unidad.codigoUnidad}</td>
            <td>
                <select class="select-estado estado-${unidad.estado}"
                        onchange="cambiarEstadoUnidad(${unidad.id}, this.value, ${idProducto}); actualizarColorEstado(this);">
                    <option value="disponible" ${unidad.estado === 'disponible' ? 'selected' : ''}>Disponible</option>
                    <option value="asignado" ${unidad.estado === 'asignado' ? 'selected' : ''}>Asignado</option>
                    <option value="dañado" ${unidad.estado === 'dañado' ? 'selected' : ''}>Dañado</option>
                    <option value="baja" ${unidad.estado === 'baja' ? 'selected' : ''}>Baja</option>
                </select>
            </td>
            <td>
                <button class="btn-eliminar" onclick="eliminarUnidad(${unidad.id}, ${idProducto})">Eliminar</button>
            </td>
        `;
        tbody.appendChild(fila);
    });
}

document.getElementById('form-unidad').addEventListener('submit', async (e) => {
    e.preventDefault();

    const idProducto = unidadProductoIdInput.value;
    const datos = new FormData();
    datos.append('codigoUnidad', document.getElementById('codigoUnidad').value);
    datos.append('estado', 'disponible');

    await fetch(`/api/productos/${idProducto}/unidades`, { method: 'POST', body: datos });

    document.getElementById('form-unidad').reset();
    unidadProductoIdInput.value = idProducto;
    await cargarUnidades(idProducto);
});

// cambia la clase de color del select cuando el usuario elige un estado distinto
function actualizarColorEstado(select) {
    select.className = 'select-estado estado-' + select.value;
}

async function cambiarEstadoUnidad(idUnidad, estado, idProducto) {
    const datos = new FormData();
    datos.append('estado', estado);
    await fetch(`/api/unidades/${idUnidad}`, { method: 'PUT', body: datos });
    await cargarUnidades(idProducto);
}

async function eliminarUnidad(idUnidad, idProducto) {
    const confirmado = await mostrarConfirmacion('¿Eliminar esta unidad?');
    if (!confirmado) return;
    const respuesta = await fetch(`/api/unidades/${idUnidad}`, { method: 'DELETE' });
    if (!respuesta.ok) {
        alert('No se pudo eliminar la unidad');
        return;
    }
    await cargarUnidades(idProducto);
}

cargarItems();

// navegacion entre vistas (SPA) 

function cambiarVista(idVista, boton) {
    document.querySelectorAll('.vista').forEach(vista => {
        vista.classList.add('oculto');
    });
    document.getElementById(idVista).classList.remove('oculto');

    document.querySelectorAll('.nav-btn').forEach(btn => btn.classList.remove('activo'));
    if (boton) boton.classList.add('activo');

    if (idVista === 'vista-auditoria') {
        document.getElementById('inputEscaner').focus();
    }
}

//  escaner de codigo de barras 

const inputEscaner = document.getElementById('inputEscaner');

inputEscaner.addEventListener('keyup', (e) => {
    if (e.key !== 'Enter') return;

    e.preventDefault();

    const codigoEscaneado = inputEscaner.value.trim();
    console.log('Código escaneado:', codigoEscaneado);

    inputEscaner.value = '';
});