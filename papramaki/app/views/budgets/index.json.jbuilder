json.array!(@budgets) do |budget|
  json.extract! budget, :id, :amount, :balance
  json.url budget_url(budget, format: :json)
end
