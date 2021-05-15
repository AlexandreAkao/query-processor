export const generateChildren = (genericGraphList: any) => {
  return genericGraphList.map((graph: any) => {
    if (graph.genericGraphList === null) {
      return {
        name: graph.algRelational
      }
    } else {
      return {
        name: graph.algRelational,
        children: generateChildren(graph.genericGraphList)
      }
    }
  })
}
