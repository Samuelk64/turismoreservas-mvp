export const getImagenPorTipo = (tipoExperiencia) => {
    const imagenes = {
        "Senderismo":  "https://images.unsplash.com/photo-1551632811-561732d1e306?w=400",
        "Naturaleza":  "https://images.unsplash.com/photo-1444464666168-49d633b86797?w=400",
        "Gastronomia": "https://images.unsplash.com/photo-1486297678162-eb2a19b0a318?w=400",
        "Cultural":    "https://images.unsplash.com/photo-1533591380348-14193f1de18f?w=400",
    }
    return imagenes[tipoExperiencia]
        ?? "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400"
}