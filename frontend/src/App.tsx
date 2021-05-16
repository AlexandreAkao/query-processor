import { ChangeEvent, useState, useEffect } from 'react';
import { RawNodeDatum } from 'react-d3-tree/lib/types/common';
import Tree from 'react-d3-tree';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import Table from './components/Table';
import api from './services/api';
import { generateChildren } from './utils/generateChildren';

import './App.css';

function App() {
  const [queryTree, setqueryTree] = useState<RawNodeDatum>({ name: '' });
  const [query, setQuery] = useState<string>("");
  const [queryVisulizer, setQueryVisulizer] = useState<string>("");
  const [tables, setTables] = useState<{ name: string, collumns: string[] }[]>([]);

  const handleQuery = async () => {
    try {
      const queryFormated = query.replaceAll("\n", " ");
      const { data } = await api.post("/query", {
        query: queryFormated
      })
      const queryTreeResponse = data['query-tree'];
      const children = generateChildren(queryTreeResponse.genericGraphList);
  
      const queryTree = {
        name: queryTreeResponse.algRelational,
        children
      }
      setQueryVisulizer(queryFormated);
      setqueryTree(queryTree)

      toast.success(`${200}: Query enviada com sucesso`, {
        position: "top-right",
        autoClose: 5000,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
      });
    } catch (error) {
      const { status, data: { error: errorResponse } } = error.response;

      toast.error(`${status}: ${errorResponse}`, {
        position: "top-right",
        autoClose: 5000,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
      });
    }
  }

  const handleChangeQuery = (event: ChangeEvent<HTMLTextAreaElement>) => setQuery(event.target.value);

  useEffect(() => {
    (async () => {
      const { data } = await api.get('/configuration');

      const tableList = Object.entries(data['table-list']);
      
      const formatedTables = tableList.map(table => ({
        name: table[0] as string, 
        collumns: table[1] as string[]
      }))
      setTables(formatedTables);
    })()
  }, []);

  return (
    <div className="App">
      <ToastContainer />
      <div id="query-container">
        <div id="input-container">
          <textarea 
            id="query-input" 
            value={query} 
            onChange={handleChangeQuery} 
            placeholder="Escreva a query aqui"
          />
          <button id="input-button" type="submit" onClick={handleQuery}>Gerar árvore</button>
        </div>
        <div id="query-content">
          <span id="query-title">Query:</span>
          <span id="query">{queryVisulizer}</span>
        </div>
        <div id="tree-container">
          <Tree 
            data={queryTree}
            separation={{
              siblings: 3
            }}
            orientation="vertical"
            branchNodeClassName="a"
            svgClassName="b"
            leafNodeClassName="c"
            rootNodeClassName="d" 
            pathFunc="step"
            translate={{
              x: 600,
              y: 50
            }}
          />
        </div>
      </div>
      <div id="info-container">
        {tables.map(table => <Table key={table.name} name={table.name} collumns={table.collumns} />)}
      </div>
    </div>
  );
}

export default App;
