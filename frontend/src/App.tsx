import { testMessage } from '@/utils/test'; // Using @ alias

function App() {
  console.log(testMessage);
  
  return (
    <div>
      <h1>Expense Tracker</h1>
    </div>
  );
}

export default App;