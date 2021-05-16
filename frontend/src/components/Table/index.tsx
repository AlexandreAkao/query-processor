import './styles.css';


const Table = ({ name, collumns }: {name: string, collumns: String[]}) => {
  return (
    <div className="table-container">
      <span className="table-title">{name}</span>
      <div className="collumns-container">
        {collumns.map(collumn => <span key={`${name}_${collumn}`}>{collumn}</span>)}
      </div>
    </div>
  )
}

export default Table
